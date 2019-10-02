package com.isnakebuzz.nicksystem.Utils.Builders;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class Book {

    private String title;
    private String author;
    private List<String> pages;

    public Book(final String title, final String author) {
        this.pages = new ArrayList<String>();
        this.title = title;
        this.author = author;
    }

    public PageBuilder addPage() {
        return new PageBuilder(this);
    }

    public ItemStack build() {
        final ItemStack book = new ItemStack(Item.getById(387));
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("author", this.author);
        tag.setString("title", this.title);
        final NBTTagList pages = new NBTTagList();
        for (final String page : this.pages) {
            pages.add(new NBTTagString(page));
        }
        tag.set("pages", pages);
        book.setTag(tag);
        return book;
    }

    public static void open(final Player p, final ItemStack book, final boolean addStats) {
        final EntityPlayer player = ((CraftPlayer) p).getHandle();
        final org.bukkit.inventory.ItemStack hand = p.getItemInHand();
        Label_0094:
        {
            try {
                p.setItemInHand(CraftItemStack.asBukkitCopy(book));
                player.playerConnection.sendPacket((Packet) new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(Unpooled.buffer())));
            } catch (Exception ex) {
                ex.printStackTrace();
                break Label_0094;
            } finally {
                p.setItemInHand(hand);
            }
            p.setItemInHand(hand);
        }
        if (addStats) {
            player.b(StatisticList.USE_ITEM_COUNT[387]);
        }
    }

    public enum ClickAction {
        Run_Command("Run_Command", 0, "run_command"),
        Suggest_Command("Suggest_Command", 1, "suggest_command"),
        Open_Url("Open_Url", 2, "open_url"),
        Change_Page("Change_Page", 3, "change_page");

        public String value;
        private String str;

        private ClickAction(final String s, final int n, final String str) {
            this.value = null;
            this.str = str;
        }

        public String getString() {
            return this.str;
        }
    }

    public enum HoverAction {
        Show_Text("Show_Text", 0, "show_text"),
        Show_Item("Show_Item", 1, "show_item"),
        Show_Entity("Show_Entity", 2, "show_entity"),
        Show_Achievement("Show_Achievement", 3, "show_achievement");

        public String value;
        private String str;

        private HoverAction(final String s, final int n, final String str) {
            this.value = null;
            this.str = str;
        }

        public String getString() {
            return this.str;
        }
    }

    public static final class PageBuilder {
        private String page;
        private boolean first;
        private Book book;

        public PageBuilder(final Book book) {
            this.page = "{text:\"\", extra:[";
            this.first = true;
            this.book = book;
        }

        public Builder add() {
            return new Builder(this);
        }

        public Builder add(final String text) {
            return new Builder(this).setText(text);
        }

        public PageBuilder newPage() {
            return this.add("\n").build();
        }

        public Book build() {
            this.book.pages.add(this.page = String.valueOf(this.page) + "]}");
            return this.book;
        }

        static /* synthetic */ void access$1(final PageBuilder pageBuilder, final boolean first) {
            pageBuilder.first = first;
        }

        static /* synthetic */ void access$3(final PageBuilder pageBuilder, final String page) {
            pageBuilder.page = page;
        }
    }

    public static final class Builder {
        private String text;
        private ClickAction click;
        private HoverAction hover;
        private PageBuilder builder;

        public Builder(final PageBuilder builder) {
            this.text = null;
            this.click = null;
            this.hover = null;
            this.builder = builder;
        }

        public Builder setText(final String text) {
            this.text = text;
            return this;
        }

        public Builder clickEvent(final ClickAction action) {
            this.click = action;
            return this;
        }

        public Builder hoverEvent(final HoverAction action) {
            this.hover = action;
            return this;
        }

        public Builder clickEvent(final ClickAction action, final String value) {
            this.click = action;
            this.click.value = value;
            return this;
        }

        public Builder hoverEvent(final HoverAction action, final String value) {
            this.hover = action;
            this.hover.value = value;
            return this;
        }

        public PageBuilder build() {
            String extra = "{text:\"" + this.text + "\"";
            if (this.click != null) {
                extra = String.valueOf(extra) + ", clickEvent:{action:" + this.click.getString() + ", value:\"" + this.click.value + "\"}";
            }
            if (this.hover != null) {
                extra = String.valueOf(extra) + ", hoverEvent:{action:" + this.hover.getString() + ", value:\"" + this.hover.value + "\"}";
            }
            extra = String.valueOf(extra) + "}";
            if (this.builder.first) {
                PageBuilder.access$1(this.builder, false);
            } else {
                extra = ", " + extra;
            }
            final PageBuilder builder = this.builder;
            PageBuilder.access$3(builder, String.valueOf(builder.page) + extra);
            return this.builder;
        }
    }
}
