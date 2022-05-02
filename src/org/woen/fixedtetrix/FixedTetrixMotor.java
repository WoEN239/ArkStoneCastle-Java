package org.woen.fixedtetrix;

/** 
 * FixedTetrix basic DC motor abstraction without encoder support. The default power at instantiation is 100%.
 * <p>Use <code>{@link FixedTetrixMotorController#getBasicMotor}</code> to retrieve a <code>TetrixMotor</code> instance.
 * 
 * @author Kirk P. Thompson
 */
public class FixedTetrixMotor implements lejos.robotics.DCMotor{
    FixedTetrixMotorController mc;
    int channel;
    
    FixedTetrixMotor(FixedTetrixMotorController mc, int channel) {
        this.mc=mc;
        this.channel=channel;
        setPower(100);
    }

    public void setPower(int power) {
        //power=Math.abs(power);
        if (power>100) power=100;
        mc.doCommand(FixedTetrixMotorController.CMD_SETPOWER, power, channel);
    }

    public int getPower() {
        return mc.doCommand(FixedTetrixMotorController.CMD_GETPOWER, 0, channel);
    }
    
    public void forward() {
        mc.doCommand(FixedTetrixMotorController.CMD_FORWARD, 0, channel);
    }

    public void backward() {
        mc.doCommand(FixedTetrixMotorController.CMD_BACKWARD, 0, channel);
    }

    public void stop() {
        mc.doCommand(FixedTetrixMotorController.CMD_STOP, 0, channel);
    }

    public void flt() {
        mc.doCommand(FixedTetrixMotorController.CMD_FLT, 0, channel);
    }
    
    /**
     * Return <code>true</code> if the motor is moving. Note that this method reports based on the current control
     * state (i.e. commanded to move) and not if the motor is actually moving. This means a motor may be stalled but this
     * method would return <code>true</code>.
     *
     * @return <code>true</code> if the motor is executing a movement command, <code>false</code> if stopped.
     */
    public boolean isMoving() {
        return FixedTetrixMotorController.MOTPARAM_OP_TRUE==mc.doCommand(FixedTetrixMotorController.CMD_ISMOVING, 0, channel);
    }

    /** 
     * Used to alter the forward/reverse direction mapping for the motor output. This is primarily intended to
     * harmonize the forward and reverse directions for motors on opposite sides of a skid-steer chassis.
     * <p>
     * Changes to this setting take effect on the next motor command.
     * @param reverse <code>true</code> to reverse direction mapping for this motor
     */
    public void setReverse(boolean reverse) {
        int op=FixedTetrixMotorController.MOTPARAM_OP_FALSE;
        if (reverse) op=FixedTetrixMotorController.MOTPARAM_OP_TRUE;
        mc.doCommand(FixedTetrixMotorController.CMD_SETREVERSE, op, channel);
    }
}
