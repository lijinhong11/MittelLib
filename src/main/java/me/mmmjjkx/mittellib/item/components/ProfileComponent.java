package me.mmmjjkx.mittellib.item.components;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.mmmjjkx.mittellib.MittelLib;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import me.mmmjjkx.mittellib.utils.Patterns;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.profile.PlayerTextures;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
public class ProfileComponent extends ReadWriteItemComponent {
    private UUID uuid;
    private String name;
    private Collection<ProfileProperty> properties;
    private ResolvableProfile.SkinPatch skinPatch;

    public static ProfileComponent fromMinecraftComponent(ResolvableProfile profile) {
        return new ProfileComponent(profile.uuid(), profile.name(), profile.properties(), profile.skinPatch());
    }

    @Override
    public void write(ConfigurationSection cs) {
        List<Map<String, String>> propertiesMap = properties.stream().map(s -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", s.getName());
            map.put("value", s.getValue());
            map.put("signature", s.getSignature());
            return map;
        }).toList();

        cs.set("id", uuid.toString());
        cs.set("name", name);
        cs.set("properties", propertiesMap);

        if (this.skinPatch != null) {
            ConfigurationSection skin = cs.createSection("skin");
            skin.set("body", this.skinPatch.body().asString());
            skin.set("cape", this.skinPatch.cape().asString());
            skin.set("elytra", this.skinPatch.elytra().asString());
            skin.set("model", this.skinPatch.model().toString());
        }
    }

    @Override
    public void read(ConfigurationSection cs) {
        String id = cs.getString("id", "null");
        this.uuid = UUID.fromString(id);

        this.name = cs.getString("name");

        ConfigurationSection skin = cs.getConfigurationSection("skin");
        if (skin != null) {
            ResolvableProfile.SkinPatchBuilder skinPatch = ResolvableProfile.SkinPatch.skinPatch();

            String bodyKey = skin.getString("body");
            if (bodyKey != null) {
                NamespacedKey body = BukkitUtils.getNamespacedKey(bodyKey);
                if (body != null) {
                    skinPatch.body(body);
                }
            }

            String capeKey = skin.getString("cape");
            if (capeKey != null) {
                NamespacedKey cape = BukkitUtils.getNamespacedKey(capeKey);
                if (cape != null) {
                    skinPatch.cape(cape);
                }
            }

            String elytraKey = skin.getString("elytra");
            if (elytraKey != null) {
                NamespacedKey elytra = BukkitUtils.getNamespacedKey(elytraKey);
                if (elytra != null) {
                    skinPatch.elytra(elytra);
                }
            }

            String modelStr = skin.getString("model");
            if (modelStr != null) {
                PlayerTextures.SkinModel model = EnumUtils.readEnum(PlayerTextures.SkinModel.class, modelStr);
                if (model == null) {

                }
            }
        }
    }

    @Override
    public void applyToItem(ItemStack item) {
        if (uuid == null) {
            MittelLib.getInstance()
                    .getLogger()
                    .severe("Cannot define a profile without an UUID");
            return;
        }

        ResolvableProfile profile = ResolvableProfile.resolvableProfile()
                .uuid(uuid)
                .name(name)
                .addProperties(properties)
                .skinPatch(skinPatch)
                .build();

        item.setData(DataComponentTypes.PROFILE, profile);
    }
}
