package teamroots.embers.upgrade;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import teamroots.embers.Embers;
import teamroots.embers.api.event.DialInformationEvent;
import teamroots.embers.api.event.UpgradeEvent;
import teamroots.embers.api.tile.IMechanicallyPowered;
import teamroots.embers.api.upgrades.IUpgradeProvider;
import teamroots.embers.block.BlockEmberGauge;
import teamroots.embers.tileentity.TileEntityCatalyticPlug;
import teamroots.embers.util.DefaultUpgradeProvider;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;

public class UpgradeCatalyticPlug extends DefaultUpgradeProvider {
    private static HashSet<Class<? extends TileEntity>> blacklist = new HashSet<>();

    public static void registerBlacklistedTile(Class<? extends TileEntity> tile) {
        blacklist.add(tile);
    }

    public UpgradeCatalyticPlug(TileEntity tile) {
        super("catalytic_plug", tile);
    }

    @Override
    public int getLimit(TileEntity tile) {
        return blacklist.contains(tile.getClass()) ? 0 : 2;
    }

    @Override
    public double transformEmberConsumption(TileEntity tile, double ember) {
        return hasCatalyst() ? ember * 2.0 : ember; //+200% if catalyst available
    }

    @Override
    public double getSpeed(TileEntity tile, double speed) {
        return hasCatalyst() ? speed * 2.0 : speed; //+200% if catalyst available
    }

    @Override
    public boolean doWork(TileEntity tile, List<IUpgradeProvider> upgrades) {
        if(hasCatalyst() && this.tile instanceof TileEntityCatalyticPlug) {
            depleteCatalyst(1);
            ((TileEntityCatalyticPlug) this.tile).setActive(20);
        }
        return false; //No cancel
    }

    private boolean hasCatalyst() {
        if(!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,null))
            return false;
        IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,null);
        return handler.drain(FluidRegistry.getFluidStack("alchemical_redstone",1),false) != null;
    }

    private void depleteCatalyst(int amt) {
        if(!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,null))
            return;
        IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,null);
        handler.drain(FluidRegistry.getFluidStack("alchemical_redstone",amt),true);
    }

    @Override
    public void throwEvent(TileEntity tile, UpgradeEvent event) {
        if(event instanceof DialInformationEvent) {
            DialInformationEvent dialEvent = (DialInformationEvent) event;
            if(hasCatalyst() && BlockEmberGauge.DIAL_TYPE.equals(dialEvent.getDialType())) {
                double speedModifier = 2.0;
                DecimalFormat multiplierFormat = Embers.proxy.getDecimalFormat("embers.decimal_format.speed_multiplier");
                dialEvent.getInformation().add(Embers.proxy.formatLocalize("embers.tooltip.upgrade.catalytic_plug", multiplierFormat.format(speedModifier))); //Proxy this because it runs in shared code
            }
        }
    }
}
