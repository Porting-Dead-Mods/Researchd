package com.portingdeadmods.researchd.api.research;

import com.portingdeadmods.portingdeadlibs.utils.UniqueArray;
import com.portingdeadmods.researchd.Researchd;
import com.portingdeadmods.researchd.impl.research.cache.CachedResearchRelations;
import com.portingdeadmods.researchd.impl.research.icons.ItemResearchIcon;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Kinda like Quest Book tabs
 *
 * @param id The unique identifier for this page
 * @param icon The icon to display for this page
 * @param iconResearchKey The researchPack key used to look up the ClientResearchIcon (usually the first root's key)
 * @param researches All researches belonging to this page
 */
public record ResearchPage(
        ResourceLocation id,
        ResearchIcon icon,
        ResourceKey<Research> iconResearchKey,
        UniqueArray<ResourceKey<Research>> researches
) {
    public static final ResourceLocation DEFAULT_PAGE_ID = Researchd.rl("default");

	public boolean containsResearch(ResourceKey<Research> res) {
		return researches.contains(res);
	}

    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    public static class Builder {
        private final ResourceLocation id;
        private ResearchIcon icon = ItemResearchIcon.EMPTY;
        private ResourceKey<Research> iconResearchKey;
        private final UniqueArray<ResourceKey<Research>> researches = new UniqueArray<>();

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder icon(ResearchIcon icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(ItemLike item) {
            this.icon = ItemResearchIcon.single(item);
            return this;
        }

        public Builder icon(ItemStack stack) {
            this.icon = ItemResearchIcon.single(stack);
            return this;
        }

        public Builder iconResearchKey(ResourceKey<Research> key) {
            this.iconResearchKey = key;
            return this;
        }

        public Builder addResearch(ResourceKey<Research> research) {
            this.researches.add(research);
            return this;
        }

        public ResearchPage build() {
            return new ResearchPage(this.id, this.icon, this.iconResearchKey, this.researches);
        }
    }
}
