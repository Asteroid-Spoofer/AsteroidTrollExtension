package me.serbob.asteroidtrollextension.actions;

import me.serbob.asteroidapi.actions.abstracts.Action;
import me.serbob.asteroidapi.actions.enums.ActionType;
import me.serbob.asteroidapi.actions.enums.Priority;
import me.serbob.asteroidapi.enums.HandEnum;
import me.serbob.asteroidapi.enums.Pose;
import me.serbob.asteroidapi.looking.Target;
import me.serbob.asteroidapi.utils.math.MathUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class TrollAction extends Action {
    private int nextMoveTick;
    private int nextToggleCrouchTick;
    private int nextPunchTick;
    private int nextLookTick;
    private int dancePhase = 0;
    private final int TOTAL_PHASES = 4;

    public TrollAction() {
        super(ActionType.IDLING, Priority.LOWEST);
    }

    @Override
    public void onStart(StartType startType) {
        resetTimers();
    }

    private void resetTimers() {
        nextMoveTick = MathUtils.randomInt(3, 8);
        nextToggleCrouchTick = MathUtils.randomInt(2, 6);
        nextPunchTick = MathUtils.randomInt(2, 5);
        nextLookTick = MathUtils.randomInt(3, 8);
    }

    @Override
    public void onUpdate() {
        if (nextMoveTick <= 0) {
            performDanceMove();
            nextMoveTick = MathUtils.randomInt(3, 8);
            dancePhase = (dancePhase + 1) % TOTAL_PHASES;
        }

        if (nextToggleCrouchTick <= 0) {
            nextToggleCrouchTick = MathUtils.randomInt(2, 6);
            getFakePlayer().getOverrides().setPose(
                    dancePhase % 2 == 0 ? Pose.SNEAKING : Pose.STANDING
            );
        }

        if (nextPunchTick <= 0) {
            nextPunchTick = MathUtils.randomInt(2, 5);
            getFakePlayer().getOverrides().swingHand(
                    dancePhase % 2 == 0 ? HandEnum.RIGHT : HandEnum.LEFT
            );
        }

        if (nextLookTick <= 0) {
            performLookAnimation();
            nextLookTick = MathUtils.randomInt(3, 8);
        }

        updateTimers();
    }

    private void performDanceMove() {
        Vector movement = new Vector();
        switch (dancePhase) {
            case 0: // Spin move
                movement.setX(0.2);
                movement.setZ(0.2);
                break;
            case 1: // Hop
                /*
                 * Normal jump checks for isGround()
                 * Force jump doesn't
                 */
                getFakePlayer().getMovement().jump();
                break;
            case 2: // Moonwalk
                movement.setZ(-0.2);
                break;
            case 3: // Side step
                movement.setX(-0.2);
                break;
        }
        getFakePlayer().move(movement);
    }

    private void performLookAnimation() {
        Location playerLoc = getFakePlayer().getEntityPlayer().getLocation();
        Vector lookVector = new Vector();

        switch (dancePhase) {
            case 0:
                lookVector.setY(2);
                break;
            case 1:
                lookVector.setX(-2);
                break;
            case 2:
                lookVector.setY(-1);
                break;
            case 3:
                lookVector.setX(2);
                break;
        }

        Location targetLoc = playerLoc.clone().add(lookVector);
        getFakePlayer().getLookController().addTarget("troll_activity",
                new Target(targetLoc, Priority.NORMAL));
    }

    private void updateTimers() {
        --nextMoveTick;
        --nextToggleCrouchTick;
        --nextPunchTick;
        --nextLookTick;
    }

    @Override
    public void onStop(StopType stopType) {
        getFakePlayer().getOverrides().setPose(Pose.STANDING);
        getFakePlayer().getLookController().removeTarget("troll_activity");
    }

    @Override
    public boolean canStart(StartType startType) {
        return true;
    }

    @Override
    public boolean canStop(StopType stopType) {
        return true;
    }
}
