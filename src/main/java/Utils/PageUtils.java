package Utils;

import Main.Main;
import com.willfp.ecoenchants.EcoEnchantsPlugin;
import com.willfp.ecoenchants.config.configs.EnchantmentConfig;
import com.willfp.ecoenchants.display.EnchantDisplay;
import com.willfp.ecoenchants.display.EnchantmentCache;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.EcoEnchants;
import com.willfp.ecoenchants.enchantments.meta.EnchantmentTarget;
import com.willfp.ecoenchants.enchantments.util.EnchantChecks;
import com.willfp.ecoenchants.enchantments.util.EnchantmentUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PageUtils {

    public static String replaceItemVars(String s, EcoEnchant e){

        String enchatable = Main.getConfigString("messages.booleans."+e.isAvailableFromTable());
        String villagers = Main.getConfigString("messages.booleans."+e.isAvailableFromVillager());
        String looting = Main.getConfigString("messages.booleans."+e.isAvailableFromLoot());
        String grindstone = Main.getConfigString("messages.booleans."+e.isGrindstoneable());


        return ColorUtils.colorMessage(s.replace("{name}", EnchantmentCache.getEntry(e).getRawName())
                .replace("{rarity}", Objects.requireNonNullElse(Main.getConfigString("messages.rarity." + e
                        .getEnchantmentRarity()
                        .getName()
                        .toLowerCase()), "&cUndefined"))
                .replace("{max-level}",String.valueOf(e.getMaxLevel()))
                .replace("{enchantment_table}", enchatable)
                .replace("{villager_trading}", villagers)
                .replace("{world_loot}", looting)
                .replace("{grindstonable}", grindstone)
        );
    }

    public static String buildAppliements(EcoEnchant e){
        StringBuilder apply = new StringBuilder();
        String applySeparator = Main.getConfigString("settings.apply-separator");
        for (int i = 0; i<e.getTargets().size()-1;i++){
            String app = ((EnchantmentTarget)e.getTargets().toArray()[i]).getName();
            apply.append(ColorUtils.colorMessage(Main.getConfigString("messages.enchantment-targets."+app))).append(applySeparator);
        }
        String app = ((EnchantmentTarget)e.getTargets().toArray()[e.getTargets().size() - 1]).getName();
        apply.append(ColorUtils.colorMessage(Main.getConfigString("messages.enchantment-targets."+app ))).append("\n");
        return apply.toString();
    }

    public static String buildConflicts(EcoEnchant e){
        StringBuilder conflicts = new StringBuilder();
        String conflictsSeparator = Main.getConfigString("settings.conflicts-separator");
        boolean ignore = Main.getConfigBoolean("settings.ignore-disabled-enchantments");

        List<Enchantment> conflictsList = new ArrayList<>();

        e.getConflicts().forEach(enchantment -> {

            EcoEnchant enchant = EcoEnchants.getByKey(enchantment.getKey());

            if (enchant == null){
                conflictsList.add(enchantment);
            }
            else {
                if (!ignore || enchant.isEnabled()){
                    conflictsList.add(enchant);
                }
            }

        });

        if (conflictsList.size()>0){
            for (int i = 0; i<conflictsList.size()-1;i++){
                conflicts.append(EnchantmentCache.getEntry(conflictsList.get(i)).getName()).append(conflictsSeparator);
            }
            conflicts.append(EnchantmentCache.getEntry(conflictsList.get(conflictsList.size() - 1)).getName()).append("\n");
        }
        else {
            conflicts = new StringBuilder(Main.getConfigString("messages.no-conflicts"));
        }
        return conflicts.toString();
    }

    public static List<String> wrapString(String string){
        return Arrays.asList(WordUtils.wrap(string, EcoEnchantsPlugin.getInstance().getConfigYml().getInt("lore.describe.wrap"), "\n", false).split("\\r?\\n"));
    }

    public static List<String> buildDescription(EcoEnchant enchant, String value){
        List<String> result = new ArrayList<>();
        for (String s : enchant.getWrappedDescription()) {
            if (s.contains("%value%")){
                String lastColor = ChatColor.getLastColors(ColorUtils.colorMessage(value)).replace(ChatColor.COLOR_CHAR, '&');
                result.add(ColorUtils.colorMessage(value.replace("{description}",
                        s
                                .replace("%value%", enchant.getPlaceholder(enchant.getMaxLevel())+lastColor)
                                .replace("&r", lastColor)

                )));
            }
            else {
                result.add(ColorUtils.colorMessage(value.replace("{description}", s)));
            }
        }
        return result;
    }

}
