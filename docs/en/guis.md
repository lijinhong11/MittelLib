GUIs
==

MittelLib provides builder-based inventory GUIs through the
[`MittelGUI`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/gui/MittelGUI.java)
interface. Click handling is wired up automatically by a global listener that
MittelLib registers on enable — you only describe the layout and the per-item
behavior.

Three GUI types are available:

- **Chest** — a static chest inventory.
- **Paginated chest** — a chest with a content area paged across multiple pages.
- **Anvil** — an anvil inventory (experimental).

## GUI items

Slots are filled with `MittelGUIItem`s. The convenient implementation is
[`ButtonItem`](https://github.com/lijinhong11/MittelLib/blob/main/src/main/java/io/github/lijinhong11/mittellib/gui/item/ButtonItem.java):

```java
// A button that runs code on click.
// The BiFunction returns whether the click is *allowed* (true) or cancelled (false).
MittelGUIItem run = ButtonItem.clickable(icon, (gui, event) -> {
    Player p = (Player) event.getWhoClicked();
    p.sendMessage("Clicked!");
    return false; // cancel the click (don't let the player take the item)
});

// A button that opens another GUI
MittelGUIItem open = ButtonItem.openGUI(icon, otherGui);

// A purely decorative, non-interactive item
MittelGUIItem filler = ButtonItem.unclickable(pane);
```

You can also implement `MittelGUIItem` directly; its `onClick` returns `true` to
allow the click and `false` to cancel it (default `true`).

## Chest GUI

Layouts are described with a **structure**: one string per row, where each
character is bound to an item. Spaces are empty slots.

```java
ChestGUI gui = MittelGUI.chestBuilder()
        .title(Component.text("Menu"))
        .structure(
                "#########",
                "#   A   #",
                "#########")
        .bind('#', ButtonItem.unclickable(filler))
        .bind('A', ButtonItem.clickable(icon, (g, e) -> { /* ... */ return false; }))
        .onOpen((player, g) -> { /* ... */ })
        .onClose((player, g) -> { /* ... */ })
        .build();

gui.open(player);
```

- Each structure row is at most 9 characters; there must be 1–6 rows.
- `size` defaults to `rows * 9` when omitted (or not a multiple of 9).
- After building you can mutate slots with `putItem(slot, item)` and
  `removeItem(slot)`.

## Paginated chest GUI

Mark one character as the **content** area; MittelLib spreads the page items
across every matching slot and handles page navigation.

```java
PaginatedChestGUI gui = MittelGUI.pagedChestBuilder()
        .title(Component.text("Shop"))
        .structure(
                "#########",
                "#OOOOOOO#",
                "#P     N#")
        .bind('#', ButtonItem.unclickable(filler))
        .content('O')                          // content slots
        .previousPage('P', ButtonItem.unclickable(prevIcon))
        .nextPage('N', ButtonItem.unclickable(nextIcon))
        .items(myItems)                        // initial page items
        .build();

gui.open(player);
```

Runtime helpers:

```java
gui.currentPage();          // 0-based
gui.pageCount();
gui.setPage(2);
gui.nextPage();
gui.previousPage();
gui.addPageItem(item);
gui.setPageItems(items);
gui.contentSlots();         // resolved content slots
```

The previous/next buttons are wired automatically — clicking them flips the page.
A content bind is required, and there must be at least one content slot, otherwise
`build()` throws.

## Anvil GUI (experimental)

```java
AnvilGUI gui = MittelGUI.anvilBuilder()
        .title(Component.text("Rename"))
        .firstItem(ButtonItem.unclickable(input))
        .resultItem(ButtonItem.clickable(result, (g, e) -> { /* ... */ return false; }))
        .prepareListener((player, view) -> {
            String text = view.getRenameText();
            // update the result item based on the typed text
        })
        .onOpen((player, g) -> { /* ... */ })
        .onClose((player, g) -> { /* ... */ })
        .build();

gui.open(player);
```

Anvil GUIs expose slots `0` (first), `1` (second) and `2` (result), and a
`prepareListener` that fires as the player types, giving access to the
`AnvilView`.

> The anvil builder is annotated `@ApiStatus.Experimental` and may change.
