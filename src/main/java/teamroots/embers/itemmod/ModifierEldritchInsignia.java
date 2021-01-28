package teamroots.embers.itemmod;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import teamroots.embers.api.EmbersAPI;
import teamroots.embers.api.itemmod.ItemModUtil;
import teamroots.embers.api.itemmod.ModifierBase;

public class ModifierEldritchInsignia extends ModifierBase {

	public ModifierEldritchInsignia() {
		super(EnumType.ARMOR,"eldritch_insignia", 0.0, true);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onEntityTarget(LivingSetAttackTargetEvent event) {
		final EntityLivingBase target = event.getTarget();
		if (!(target instanceof EntityPlayer))
			return;
		final int level = ItemModUtil.getArmorModifierLevel(target, EmbersAPI.ELDRITCH_INSIGNIA);
		if (level <= 0)
			return;
		final EntityLivingBase entity = event.getEntityLiving();
		if (entity instanceof EntityPlayer)
			return;
		Class NPCClass = null;
		try {
			NPCClass = Class.forName("noppes.npcs.entity.EntityNPCInterface", false, this.getClass().getClassLoader());
		} catch (ClassNotFoundException | LinkageError ignored) {}
		if (NPCClass != null && NPCClass.isInstance(entity))
			return;
		final DamageSource lastSource = entity.getLastDamageSource();
		if ((lastSource == null
				|| lastSource.getTrueSource() == null
				|| lastSource.getTrueSource().getUniqueID().compareTo(target.getUniqueID()) != 0
		) && entity.getEntityId() % (3 + level) >= 2) {
				((EntityLiving) entity).setAttackTarget(null);
				//EmberInventoryUtil.removeEmber((EntityPlayer)event.getTarget(), cost);
			}
	}
}
