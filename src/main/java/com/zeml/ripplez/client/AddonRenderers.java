package com.zeml.ripplez.client;

import com.github.standobyte.jojo.client.ui.StandStatsRenderer;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.AddonEntityTypes;
import com.zeml.ripplez.init.power.AddonStands;
import com.zeml.ripplez.jojoimp.stands.white_snake.client.renderer.DiscOutLayer;
import com.zeml.ripplez.jojoimp.stands.white_snake.client.renderer.ThrowDiscRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static com.github.standobyte.jojo.client.ModEntityTypeRenderers.castToHumanoid;

@EventBusSubscriber(modid = RipplesAddon.MOD_ID, value = Dist.CLIENT)
public class AddonRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(AddonEntityTypes.THREW_DISC.get(), ThrowDiscRenderer::new);
    }


    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        EntityRendererProvider.Context context = event.getContext();
        var renderers = Minecraft.getInstance().getEntityRenderDispatcher();
        for (var renderer : renderers.renderers.values()) {
            castToHumanoid(renderer).ifPresent(a->AddonRenderers.addHumanoidLayers(a,context));
        }
        for (var playerRenderer : renderers.getSkinMap().values()) {
            castToHumanoid(playerRenderer).ifPresent(a->AddonRenderers.addHumanoidLayers(a,context));
        }
    }
    private static <T extends LivingEntity, M extends HumanoidModel<T>> void addHumanoidLayers(LivingEntityRenderer<T, M> renderer, EntityRendererProvider.Context context) {
        renderer.addLayer(new DiscOutLayer<>(renderer,context.getItemInHandRenderer()));

    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientSetup0(FMLClientSetupEvent event){
        whiteSnakeStats();
        greenDayStats();
    }

    public static void whiteSnakeStats(){
        StandStatsRenderer.overrideCosmeticStats(AddonStands.WHITE_SNAKE.getId(),new StandStatsRenderer.CosmeticStandStats(){
            @Override
            public String statRankLetter(StandStatsRenderer.HexagonStandStat stat, StandPower standData, double statConvertedValue) {
                if (stat.name().equals(StandStatsRenderer.HexagonStandStat.STRENGTH.name()) ||
                        stat.name().equals(StandStatsRenderer.HexagonStandStat.RANGE.name()) ||
                        stat.name().equals(StandStatsRenderer.HexagonStandStat.PRECISION.name())) {
                    return "?";
                }
                if(stat.name().equals(StandStatsRenderer.HexagonStandStat.SPEED.name())){
                    return "D";
                }
                return super.statRankLetter(stat, standData, statConvertedValue);
            }

            @Override
            public float statConvertedValue(StandStatsRenderer.HexagonStandStat stat, StandPower standData, StandStats stats, float statLeveling) {
                if (stat.name().equals(StandStatsRenderer.HexagonStandStat.STRENGTH.name()) ||
                        stat.name().equals(StandStatsRenderer.HexagonStandStat.RANGE.name()) ||
                        stat.name().equals(StandStatsRenderer.HexagonStandStat.PRECISION.name())){
                    return 0;
                }
                if(stat.name().equals(StandStatsRenderer.HexagonStandStat.SPEED.name())){
                    return 2;
                }

                return super.statConvertedValue(stat, standData, stats, statLeveling);
            }
        });
    }

    public static void greenDayStats(){
        StandStatsRenderer.overrideCosmeticStats(AddonStands.GREEN_DAY.getId(),new StandStatsRenderer.CosmeticStandStats(){
            @Override
            public String statRankLetter(StandStatsRenderer.HexagonStandStat stat, StandPower standData, double statConvertedValue) {
                if(stat.name().equals(StandStatsRenderer.HexagonStandStat.RANGE.name())){
                    return "A";
                }
                return super.statRankLetter(stat, standData, statConvertedValue);
            }

            @Override
            public float statConvertedValue(StandStatsRenderer.HexagonStandStat stat, StandPower standData, StandStats stats, float statLeveling) {
                if(stat.name().equals(StandStatsRenderer.HexagonStandStat.RANGE.name())){
                    return 20;
                }

                return super.statConvertedValue(stat, standData, stats, statLeveling);
            }
        });
    }
}
