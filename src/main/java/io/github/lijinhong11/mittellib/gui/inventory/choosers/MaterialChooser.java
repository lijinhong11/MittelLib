package io.github.lijinhong11.mittellib.gui.inventory.choosers;

import io.github.lijinhong11.mittellib.MittelLib;
import io.github.lijinhong11.mittellib.gui.inventory.MittelGUI;
import io.github.lijinhong11.mittellib.gui.inventory.impl.PaginatedChestGUI;
import io.github.lijinhong11.mittellib.gui.inventory.item.ButtonItem;
import io.github.lijinhong11.mittellib.gui.inventory.item.MittelGUIItem;
import io.github.lijinhong11.mittellib.hook.ContentProviders;
import io.github.lijinhong11.mittellib.iface.block.PackedBlock;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import org.bukkit.entity.Player;

public class MaterialChooser {
    private static final BiPredicate<String, MittelGUIItem> LOOKUP;

    static {
        LOOKUP = (s, i) -> i.getItem().getType().toString().contains(s.toUpperCase());
    }

    public static void openUsableBlockChooser(Player p, Consumer<PackedBlock> blockConsumer) {
        PaginatedChestGUI gui = MittelGUI.pagedChestBuilder()
                .structure("BBBBBBBSB", "BMMMMMMMB", "BMMMMMMMB", "BMMMMMMMB", "BMMMMMMMB", "BBPBBBNBB")
                .bind('B', ButtonItem.BACKGROUND)
                .bindSearch(
                        'S',
                        ButtonItem.getSearchButton(
                                MittelLib.getInstance().getLanguageManager().getMsgComponent(p, "common.search")))
                .content('M')
                .onSearch(LOOKUP)
                .previousPage(
                        'P',
                        ButtonItem.getPageButton(
                                MittelLib.getInstance().getLanguageManager().getMsgComponent(p, "common.previous-page")))
                .nextPage(
                        'N',
                        ButtonItem.getPageButton(
                                MittelLib.getInstance().getLanguageManager().getMsgComponent(p, "common.next-page")))
                .build();

        List<PackedBlock> usableBlocks = ContentProviders.getAllUsableBlocks();
        usableBlocks = usableBlocks.stream()
                .filter(u -> u.toItem() != null && !u.toItem().getType().isAir())
                .toList();

        gui.setPageItems(usableBlocks.stream()
                .map(b -> ButtonItem.clickable(b.toItem(), (g, e) -> {
                    blockConsumer.accept(b);
                    p.closeInventory();
                    return false;
                }))
                .toList());

        gui.open(p);
    }
}
