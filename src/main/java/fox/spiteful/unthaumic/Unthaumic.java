package fox.spiteful.unthaumic;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;

import java.lang.reflect.Field;
import java.util.HashMap;

@Mod(modid = "Unthaumic", name = "Thaumcraft Minus Thaumcraft", dependencies = "required-after:Thaumcraft")
public class Unthaumic {

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
}