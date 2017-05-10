package eyeq.pinkslime.entity.monster;

import eyeq.util.entity.EntityUtils;
import eyeq.util.entity.IEntityRideablePlayer;
import eyeq.util.entity.player.EntityPlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class EntitySlimePink extends EntitySlime implements IEntityRideablePlayer {
    private EntityPlayer ridingPlayer;

    public EntitySlimePink(World world) {
        super(world);
    }

    @Override
    protected EntitySlime createInstance() {
        return new EntitySlimePink(world);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected void damageEntity(DamageSource source, float amount) {
        ridingPlayer = null;
        dismountRidingEntity();
        super.damageEntity(source, amount);
    }

    @Override
    public double getYOffset() {
        return this.height;
    }

    @Override
    public void applyEntityCollision(Entity entity) {
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
    }

    @Override
    protected int getAttackStrength() {
        return super.getAttackStrength() - 1;
    }

    public void updatePassenger(Entity passenger) {
        System.out.println(this.isPassenger(passenger));
        super.updatePassenger(passenger);
    }

    @Override
    public void onUpdate() {
        if(this.hurtResistantTime == 0) {
            Entity target = this.getAttackTarget();
            if(target != null && target.getEntityBoundingBox().intersectsWith(this.getEntityBoundingBox())) {
                if(ridingPlayer == null && this.getRidingEntity() == null) {
                    while(true) {
                        List<Entity> passengers = target.getPassengers();
                        if(!passengers.isEmpty()) {
                            target = passengers.get(0);
                        } else if(target instanceof EntityPlayer) {
                            Entity temp = EntityPlayerUtils.PASSENGER.get(((EntityPlayer) target));
                            if(temp == null) {
                                break;
                            }
                            target = temp;
                        } else {
                            break;
                        }
                    }
                    if(target instanceof EntityPlayer) {
                        ridingPlayer = (EntityPlayer) target;
                    } else {
                        startRiding(target);
                    }
                }
            }
        }
        if(ridingPlayer != null) {
            if(ridingPlayer.isDead) {
                EntityPlayerUtils.PASSENGER.remove(ridingPlayer);
                ridingPlayer = null;
            } else {
                if(!world.isRemote) {
                    if(isRiding()) {
                        dismountRidingEntity();
                    }
                }
                EntityUtils.setRidingEntity(this, ridingPlayer);
            }
        }
        Entity ridingEntity = getRidingEntity();
        if(ridingEntity != null && ridingEntity instanceof EntityLivingBase) {
            while(true) {
                if(ridingEntity.getRidingEntity() != null && ridingEntity.getRidingEntity() instanceof EntityLivingBase) {
                    ridingEntity = ridingEntity.getRidingEntity();
                } else if(ridingEntity instanceof IEntityRideablePlayer && ((IEntityRideablePlayer) ridingEntity).getRidingPlayer() != null) {
                    ridingEntity = ((IEntityRideablePlayer) ridingEntity).getRidingPlayer();
                } else {
                    break;
                }
            }
            if(!(ridingEntity instanceof EntitySlime)) {
                // dealDamage
                if(ridingEntity.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength())) {
                    this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    this.applyEnchantments(this, ridingEntity);
                }
            }
        }
        if(ridingPlayer == null) {
            super.onUpdate();
            return;
        }
        // updateRidden
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        super.onUpdate();
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        this.onGroundSpeedFactor = 0.0F;
        this.fallDistance = 0.0F;
        this.setPosition(ridingPlayer.posX, ridingPlayer.posY + ridingPlayer.getMountedYOffset() + this.getYOffset(), ridingPlayer.posZ);
        EntityUtils.setRidingEntity(this, null);
    }

    public boolean getCanSpawnHereEntityLiving() {
        IBlockState state = this.world.getBlockState((new BlockPos(this)).down());
        return state.canEntitySpawn(this);
    }

    @Override
    public boolean getCanSpawnHere() {
        BlockPos pos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
        Chunk chunk = this.world.getChunkFromBlockCoords(pos);
        if(this.world.getWorldInfo().getTerrainType().handleSlimeSpawnReduction(rand, world)) {
            return false;
        }
        if(this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            if(this.posY > 50.0 && this.posY < 70.0 && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < this.world.getCurrentMoonPhaseFactor() && this.world.getLightFromNeighbors(new BlockPos(this)) <= this.rand.nextInt(8)) {
                return getCanSpawnHereEntityLiving();
            }
            if(this.rand.nextInt(10) == 0 && chunk.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0) {
                return getCanSpawnHereEntityLiving();
            }
        }
        return false;
    }

    @Override
    protected void jump() {
        this.motionY = 0.6;
        this.isAirBorne = true;
    }

    @Override
    public EntityPlayer getRidingPlayer() {
        return ridingPlayer;
    }
}
