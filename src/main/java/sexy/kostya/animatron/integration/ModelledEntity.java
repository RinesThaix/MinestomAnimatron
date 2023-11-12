package sexy.kostya.animatron.integration;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import org.jetbrains.annotations.NotNull;
import sexy.kostya.animatron.model.Model;

import java.util.concurrent.CompletableFuture;

public class ModelledEntity extends Entity {

    private final ModelView view;
    private final int       driverBoneID;

    public ModelledEntity(@NotNull Model model) {
        super(EntityType.ZOMBIE);
        this.view = new ModelView(model, this);
        this.driverBoneID = this.view.getBoneID("mount");
    }

    public final ModelView getView() {
        return view;
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        view.getInjector().tick();
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        super.updateNewViewer(player);
        view.getInjector().addViewer(player);
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);
        view.getInjector().removeViewer(player);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return view.getInjector().setInstance(instance, spawnPosition, super.setInstance(instance, spawnPosition));
    }

    @Override
    public void remove() {
        view.getInjector().remove();
        super.remove();
    }

    @Override
    public void synchronizePosition(boolean includeSelf) {
        super.synchronizePosition(includeSelf);
        view.getInjector().synchronizePosition();
    }

    @Override
    public void refreshPosition(@NotNull Pos newPosition, boolean ignoreView) {
        view.getInjector().refreshPosition(
                newPosition,
                ignoreView,
                super::refreshPosition
        );
    }

    @Override
    protected @NotNull SetPassengersPacket getPassengersPacket() {
        if (driverBoneID == -1) {
            return super.getPassengersPacket();
        } else {
            return new SetPassengersPacket(driverBoneID, getPassengers().stream().map(Entity::getEntityId).toList());
        }
    }

}
