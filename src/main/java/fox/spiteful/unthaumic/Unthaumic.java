package fox.spiteful.unthaumic;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

@Mod(modid = "Unthaumic", name = "Thaumcraft Minus Thaumcraft", dependencies = "required-after:Thaumcraft")
public class Unthaumic {

    @EventHandler
    public void doTheWindyThing(FMLPostInitializationEvent event){

        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void minus(FMLLoadCompleteEvent event) {
        for(ResearchCategoryList tab : ResearchCategories.researchCategories.values()){
            if(tab != null) {
                for (ResearchItem item : tab.research.values()) {
                    if(item != null){
                        item.setAutoUnlock();
                    }
                }
            }
        }
        try {
            Field warpMap = ThaumcraftApi.class.getDeclaredField("warpMap");
            warpMap.setAccessible(true);
            HashMap<Object, Integer> warpList = (HashMap<Object, Integer>)warpMap.get(null);
            warpList.clear();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void join(EntityJoinWorldEvent event){
        if(!event.world.isRemote && event.entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer)event.entity;
            NBTTagCompound tags = player.getEntityData();
            NBTTagCompound persist;
            if(tags.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
                persist = tags.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            else {
                persist = new NBTTagCompound();
                tags.setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);
            }
            if(!persist.getBoolean("Unthaumic")){

                for(Aspect aspect : Aspect.getCompoundAspects()){
                    Thaumcraft.proxy.playerKnowledge.addDiscoveredAspect(player.getCommandSenderName(), aspect);
                }
                for(List list : ThaumcraftApi.objectTags.keySet()){
                    if(list.size() == 2 && list.get(0) instanceof Item){
                        Item item = (Item)list.get(0);
                        if(list.get(1) instanceof Integer) {
                            int meta = (Integer) list.get(1);
                            if(meta == OreDictionary.WILDCARD_VALUE) {
                                for (meta = 0; meta < 16; meta++) {
                                    ResearchManager.completeScannedObjectUnsaved(player.getCommandSenderName(), (new StringBuilder()).append("@").append(ScanManager.generateItemHash(item, meta)).toString());
                                }
                            }
                            else
                                ResearchManager.completeScannedObjectUnsaved(player.getCommandSenderName(), (new StringBuilder()).append("@").append(ScanManager.generateItemHash(item, meta)).toString());
                        }
                        else if(list.get(1) instanceof int[]){
                            int[] metas = (int[])list.get(1);
                            for(int meta : metas){
                                ResearchManager.completeScannedObjectUnsaved(player.getCommandSenderName(), (new StringBuilder()).append("@").append(ScanManager.generateItemHash(item, meta)).toString());
                            }
                        }
                    }
                }
                ResearchManager.scheduleSave(player);

                persist.setBoolean("Unthaumic", true);
            }
        }
    }
}