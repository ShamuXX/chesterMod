package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.SectionPos;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BlendingDataFix extends DataFix {
   private final String name;
   private static final Set<String> STATUSES_TO_SKIP_BLENDING = Set.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:biomes");

   public BlendingDataFix(Schema p_216561_) {
      super(p_216561_, false);
      this.name = "Blending Data Fix v" + p_216561_.getVersionKey();
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(References.CHUNK);
      return this.fixTypeEverywhereTyped(this.name, type, (p_216563_) -> {
         return p_216563_.update(DSL.remainderFinder(), BlendingDataFix::updateChunkTag);
      });
   }

   private static Dynamic<?> updateChunkTag(Dynamic<?> p_216565_) {
      p_216565_ = p_216565_.remove("blending_data");
      Optional<? extends Dynamic<?>> optional = p_216565_.get("Status").result();
      if (optional.isPresent()) {
         String s = NamespacedSchema.ensureNamespaced(optional.get().asString("empty"));
         Optional<? extends Dynamic<?>> optional1 = p_216565_.get("below_zero_retrogen").result();
         if (!STATUSES_TO_SKIP_BLENDING.contains(s)) {
            p_216565_ = updateBlendingData(p_216565_, 384, -64);
         } else if (optional1.isPresent()) {
            Dynamic<?> dynamic = optional1.get();
            String s1 = NamespacedSchema.ensureNamespaced(dynamic.get("target_status").asString("empty"));
            if (!STATUSES_TO_SKIP_BLENDING.contains(s1)) {
               p_216565_ = updateBlendingData(p_216565_, 256, 0);
            }
         }
      }

      return p_216565_;
   }

   private static Dynamic<?> updateBlendingData(Dynamic<?> p_216567_, int p_216568_, int p_216569_) {
      return p_216567_.set("blending_data", p_216567_.createMap(Map.of(p_216567_.createString("min_section"), p_216567_.createInt(SectionPos.blockToSectionCoord(p_216569_)), p_216567_.createString("max_section"), p_216567_.createInt(SectionPos.blockToSectionCoord(p_216569_ + p_216568_)))));
   }
}