package me.mmmjjkx.mittellib.item.components.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.item.components.internal.ItemComponentSpec;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import me.mmmjjkx.mittellib.utils.enums.MCVersion;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.*;

@ItemComponentSpec(key = "tool", requiredVersion = MCVersion.V1_20_5)
@NoArgsConstructor
@AllArgsConstructor
public class ToolComponent extends ReadWriteItemComponent {
    private @NonNegative float defaultMiningSpeed = 1;
    private @NonNegative int damagePerBlock = 1;
    private boolean canDestroyBlocksInCreative = true;
    private List<Tool.Rule> rules = new ArrayList<>();

    public static ToolComponent fromMinecraftComponent(Tool tool) {
        return new ToolComponent(tool.defaultMiningSpeed(), tool.damagePerBlock(), tool.canDestroyBlocksInCreative(), tool.rules());
    }

    @Override
    public void applyToItem(ItemStack item) {
        Tool tool = Tool.tool()
                .defaultMiningSpeed(defaultMiningSpeed)
                .damagePerBlock(damagePerBlock)
                .canDestroyBlocksInCreative(canDestroyBlocksInCreative)
                .addRules(rules)
                .build();

        item.setData(DataComponentTypes.TOOL, tool);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("defaultMiningSpeed", defaultMiningSpeed);
        cs.set("damagePerBlock", damagePerBlock);
        cs.set("canDestroyBlocksInCreative", canDestroyBlocksInCreative);

        if (rules != null && !rules.isEmpty()) {
            List<Map<String, Object>> rules = this.rules.stream().map(r -> {
               Map<String, Object> map = new HashMap<>();
               map.put("blocks", r.blocks().values().stream().map(Key::asString).toList());
               map.put("correctForDrops", r.correctForDrops().toString());

               if (r.speed() != null) {
                   map.put("speed", r.speed());
               }

               return map;
            }).toList();

            cs.set("rules", rules);
        }
    }

    public static ToolComponent readFromSection(ConfigurationSection cs) {
        float defaultMiningSpeed = (float) cs.getDouble("defaultMiningSpeed");
        int damagePerBlock = cs.getInt("damagePerBlock");
        boolean canDestroyBlocksInCreative = cs.getBoolean("canDestroyBlocksInCreative");

        List<Tool.Rule> rules = new ArrayList<>();
        List<Map<?, ?>> rawRules = cs.getMapList("rules");
        if (!rawRules.isEmpty()) {
            for (Map<?, ?> rawRule : rawRules) {
                Map<String, Object> map = (Map<String, Object>) rawRule;
                List<String> rawKeys = (List<String>) map.get("blocks");
                List<TypedKey<BlockType>> keys = BukkitUtils.getNamespacedKeys(rawKeys).stream().map(RegistryKey.BLOCK::typedKey).toList();
                RegistryKeySet<BlockType> regSet = RegistrySet.keySet(RegistryKey.BLOCK, keys);

                Float speed = null;
                if (map.containsKey("speed")) {
                    speed = (Float) map.get("speed");
                }

                TriState triState = TriState.NOT_SET;
                if (map.containsKey("correctForDrops")) {
                    triState = EnumUtils.readTriState((String) map.get("correctForDrops"));
                }

                rules.add(Tool.rule(regSet, speed, triState));
            }
        }

        return new ToolComponent(defaultMiningSpeed, damagePerBlock, canDestroyBlocksInCreative, rules);
    }
}
