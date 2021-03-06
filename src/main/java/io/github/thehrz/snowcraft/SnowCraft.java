package io.github.thehrz.snowcraft;

import io.github.thehrz.snowcraft.object.machines.OriginalAutomatedCraftingChamber;
import io.github.thehrz.snowcraft.setup.ItemsSetup;
import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.metrics.BStats;
import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TInject;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItemSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

import static io.izzel.taboolib.module.locale.chatcolor.TColor.translate;

/**
 * @author Thehrz
 */
public final class SnowCraft extends Plugin {
    private static String prefix;

    @TInject("config.yml")
    public static TConfig conf;

    @NotNull
    public static TConfig getConf() {
        return conf;
    }

    @NotNull
    public static String getPrefix() {
        return translate(prefix);
    }

    public static void setPrefix(String prefix) {
        SnowCraft.prefix = prefix;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this.getPlugin());
    }

    @Override
    public void onEnable() {
        OriginalAutomatedCraftingChamber.updateBlackList();
        conf.listener(() -> {
            OriginalAutomatedCraftingChamber.updateBlackList();
            setPrefix(getConf().getString("Prefix"));
            System.out.println(getPrefix() + " §a配置文件加载成功");
        });
        setPrefix(getConf().getString("Prefix"));
        new BStats(this.getPlugin());
        iteratorRecipes();
        ItemsSetup.setupItems();
    }

    private void iteratorRecipes() {
        Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            StringBuilder builder = new StringBuilder();
            Recipe recipe = recipeIterator.next();
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                Map<Character, ItemStack> ingredientMap = shapedRecipe.getIngredientMap();
                for (int i = 0; i < 3; i++) {
                    int n = 0;
                    String row = " ";
                    if (shapedRecipe.getShape().length == 3 || i < shapedRecipe.getShape().length) {
                        row = shapedRecipe.getShape()[i];
                    }
                    StringBuilder stringBuilderRow = new StringBuilder();
                    for (int j = 0; j < 3; j++) {
                        ItemStack item = null;
                        if (row.length() == 3 || j < row.length()) {
                            item = ingredientMap.get(row.charAt(j));
                        }
                        if (item == null) {
                            n++;
                        }
                        stringBuilderRow.append(" </slot> ");
                        stringBuilderRow.append(CustomItemSerializer.serialize(item, CustomItemSerializer.ItemFlag.DATA, CustomItemSerializer.ItemFlag.ITEMMETA_DISPLAY_NAME, CustomItemSerializer.ItemFlag.ITEMMETA_LORE, CustomItemSerializer.ItemFlag.MATERIAL));
                    }
                    if (n != 3) {
                        builder.append(stringBuilderRow);
                    }
                }
                OriginalAutomatedCraftingChamber.recipes.put(builder.toString(), shapedRecipe.getResult());
            }
        }
    }
}
