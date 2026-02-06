package me.mmmjjkx.mittellib.item.components;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.mmmjjkx.mittellib.MittelLib;
import me.mmmjjkx.mittellib.configuration.ReadWriteItemComponent;
import me.mmmjjkx.mittellib.utils.BukkitUtils;
import me.mmmjjkx.mittellib.utils.EnumUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.profile.PlayerTextures;

import java.util.*;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@AllArgsConstructor
public class ProfileComponent extends ReadWriteItemComponent {
    private final UUID uuid;
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

    public static ProfileComponent readFromSection(ConfigurationSection cs) {
        String id = cs.getString("id", "null");
        UUID uuid = UUID.fromString(id);
        String name = cs.getString("name");

        List<Map<?, ?>> propertiesMap = cs.getMapList("properties");
        List<ProfileProperty> properties = new ArrayList<>();
        if (!propertiesMap.isEmpty()) {
            properties = propertiesMap.stream().map(m -> {
                Map<String, String> map = (Map<String, String>) m;
                return new ProfileProperty(map.get("name"), map.get("value"), map.get("signature"));
            }).toList();
        }

        ResolvableProfile.SkinPatch skinPatch = ResolvableProfile.SkinPatch.empty();
        ConfigurationSection skin = cs.getConfigurationSection("skin");
        if (skin != null) {
            ResolvableProfile.SkinPatchBuilder skinPatchBuilder = ResolvableProfile.SkinPatch.skinPatch();

            String bodyKey = skin.getString("body");
            if (bodyKey != null) {
                NamespacedKey body = BukkitUtils.getNamespacedKey(bodyKey);
                if (body != null) {
                    skinPatchBuilder.body(body);
                }
            }

            String capeKey = skin.getString("cape");
            if (capeKey != null) {
                NamespacedKey cape = BukkitUtils.getNamespacedKey(capeKey);
                if (cape != null) {
                    skinPatchBuilder.cape(cape);
                }
            }

            String elytraKey = skin.getString("elytra");
            if (elytraKey != null) {
                NamespacedKey elytra = BukkitUtils.getNamespacedKey(elytraKey);
                if (elytra != null) {
                    skinPatchBuilder.elytra(elytra);
                }
            }

            String modelStr = skin.getString("model");
            if (modelStr != null) {
                PlayerTextures.SkinModel model = EnumUtils.readEnum(PlayerTextures.SkinModel.class, modelStr);
                if (model == null) {
                    MittelLib.getInstance()
                            .getLogger()
                            .severe("Cannot find a skin model with the name " + modelStr
                            + "! Available options are SLIM and CLASSIC");
                } else {
                    skinPatchBuilder.model(model);
                }
            }

            skinPatch = skinPatchBuilder.build();
        }

        return new ProfileComponent(uuid, name, properties, skinPatch);
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
