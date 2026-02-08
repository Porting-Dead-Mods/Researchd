package com.portingdeadmods.researchd.client.screens.editor.widgets;

import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.api.editor.PackLocation;
import com.portingdeadmods.researchd.client.screens.lib.widgets.DropDownWidget;
import com.portingdeadmods.researchd.networking.editor.SetPackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SelectPackDropDownWidget extends DropDownWidget<SelectPackSearchBarWidget> {
    private final SelectPackSearchBarWidget packSearchBarWidget;
    private final List<PackLocation> availablePacks;

    public SelectPackDropDownWidget(SelectPackSearchBarWidget packSearchBarWidget, PackType type) {
        this.packSearchBarWidget = packSearchBarWidget;
        IntegratedServer singleplayerServer = Minecraft.getInstance().getSingleplayerServer();
        if (singleplayerServer != null && type == PackType.SERVER_DATA) {
            Path datapacksPath = singleplayerServer.getWorldPath(LevelResource.DATAPACK_DIR);
            this.availablePacks = collectAvailablePacks(datapacksPath, type);
        } else if (type == PackType.CLIENT_RESOURCES) {
            this.availablePacks = collectAvailablePacks(Minecraft.getInstance().getResourcePackDirectory(), type);
        } else {
            this.availablePacks = new ArrayList<>();
        }
    }

    private static List<PackLocation> collectAvailablePacks(Path packsDirectoryPath, PackType type) {
        try (Stream<Path> stream = Files.list(packsDirectoryPath)) {
            List<PackLocation> packs = new ArrayList<>();
            stream.filter(Files::isDirectory).forEach(datapackPath -> {
                Path typePath = datapackPath.resolve(type.getDirectory());
                if (Files.exists(typePath) && Files.isDirectory(typePath)) {
                    try (Stream<Path> namespaceDirectories = Files.list(typePath)) {
                        namespaceDirectories.forEach(namespacePath -> {
                            PackLocation packLocation = new PackLocation(datapackPath, namespacePath.getFileName().toString(), type);
                            packs.add(packLocation);
                        });
                    } catch (Exception e) {
                        Researchd.LOGGER.error("Encountered error trying to collect available packs", e);
                    }
                }
            });
            return packs;
        } catch (Exception e) {
            Researchd.LOGGER.error("Encountered error trying to collect available packs", e);
        }
        return List.of();
    }

    @Override
    protected void buildOptions() {
        for (PackLocation pack : this.availablePacks) {
            this.addOption(new PackOption(pack));
        }
    }

    @Override
    protected void optionClicked(Option option, int mouseX, int mouseY) {
        super.optionClicked(option, mouseX, mouseY);

        if (option instanceof PackOption packOption) {
            PacketDistributor.sendToServer(new SetPackPayload(packOption.packLocation));
        }
    }

    private record PackOption(String display, PackLocation packLocation, Font font) implements Option {
        private PackOption(PackLocation packLocation) {
            this(packLocation.rootPackName() + "/" + packLocation.namespace(), packLocation, Minecraft.getInstance().font);
        }

        @Override
        public int width() {
            return this.font().width(this.display);
        }

        @Override
        public int height() {
            return this.font().lineHeight;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks, OptionContext context) {
            if (this.isHovered(x, y, mouseX, mouseY, context)) {
                guiGraphics.fill(x - 1, y - 1, x + context.maxWidth() - 1, y + this.height() + 1, FastColor.ARGB32.color(120, 120, 120));
            }

            guiGraphics.drawString(this.font(), this.display, x, y, -1);
        }
    }

}
