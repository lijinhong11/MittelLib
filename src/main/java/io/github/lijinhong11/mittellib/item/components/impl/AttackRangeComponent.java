package io.github.lijinhong11.mittellib.item.components.impl;

import io.github.lijinhong11.mittellib.configuration.ReadWriteItemComponent;
import io.github.lijinhong11.mittellib.item.components.internal.ItemComponentSpec;
import io.github.lijinhong11.mittellib.utils.NumberUtils;
import io.github.lijinhong11.mittellib.utils.enums.MCVersion;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Range;

@ItemComponentSpec(key = " attackRange", requiredVersion = MCVersion.V1_21_11)
@NoArgsConstructor
@AllArgsConstructor
public class AttackRangeComponent extends ReadWriteItemComponent {
    private @Range(from = 0, to = 64) float minReach = 0;
    private @Range(from = 0, to = 64) float maxReach = 3;
    private @Range(from = 0, to = 64) float minCreativeReach = 0;
    private @Range(from = 0, to = 64) float maxCreativeReach = 5;
    private @Range(from = 0, to = 1) float hitboxMargin = 0.3f;
    private @Range(from = 0, to = 2) float mobFactor = 1;

    public static AttackRangeComponent fromMinecraftComponent(AttackRange attackRange) {
        return new AttackRangeComponent(attackRange.minReach(), attackRange.maxReach(), attackRange.minCreativeReach(), attackRange.maxCreativeReach(), attackRange.hitboxMargin(), attackRange.mobFactor());
    }

    public static DataComponentType getDataComponentType() {
        return DataComponentTypes.ATTACK_RANGE;
    }

    public static AttackRangeComponent readFromSection(ConfigurationSection cs) {
        float minReach = NumberUtils.asUnsigned((float) cs.getDouble("minReach", 0));
        float maxReach = NumberUtils.asUnsigned((float) cs.getDouble("maxReach", 3));
        float minCreativeReach = NumberUtils.asUnsigned((float) cs.getDouble("minCreativeReach", 0));
        float maxCreativeReach = NumberUtils.asUnsigned((float) cs.getDouble("maxCreativeReach", 5));
        float hitboxMargin = NumberUtils.asUnsigned((float) cs.getDouble("hitboxMargin", 0.3));
        float mobFactor = NumberUtils.asUnsigned((float) cs.getDouble("mobFactor", 1));

        return new AttackRangeComponent(minReach, maxReach, minCreativeReach, maxCreativeReach, hitboxMargin, mobFactor);
    }

    @Override
    public void applyToItem(ItemStack item) {
        AttackRange attackRange = AttackRange.attackRange()
                .minReach(minReach)
                .maxReach(maxReach)
                .minCreativeReach(minCreativeReach)
                .maxCreativeReach(maxCreativeReach)
                .hitboxMargin(hitboxMargin)
                .mobFactor(mobFactor)
                .build();

        item.setData(DataComponentTypes.ATTACK_RANGE, attackRange);
    }

    @Override
    public void write(ConfigurationSection cs) {
        cs.set("minReach", minReach);
        cs.set("maxReach", maxReach);
        cs.set("minCreativeReach", minCreativeReach);
        cs.set("maxCreativeReach", maxCreativeReach);
        cs.set("hitboxMargin", hitboxMargin);
        cs.set("mobFactor", mobFactor);
    }
}
