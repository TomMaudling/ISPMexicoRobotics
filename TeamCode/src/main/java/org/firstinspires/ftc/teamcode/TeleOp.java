/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * Git Bash stuff:
 * First go to the repository
 * Then initialize git (git init)
 * Then add (git add .)
 * Then commit (git commit -m "")-
 * Then push to master (git push origin master) --> not necessary
 */

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group="Iterative Opmode")
//Disabled
public class TeleOp extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Basic_Bot robo = new Basic_Bot();
    int toggle = 0;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        robo.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        // region Driving code BELOW (single stick)     (gp1)
        double drive = gamepad1.left_stick_y;
        double turn = gamepad1.left_stick_x;

        double leftRubbPower = Range.clip(drive - turn, -1.0, 1.0);    // driving powers defined and set
        double rightRubbPower = Range.clip(drive + turn, -1.0, 1.0);

        robo.leftDrive.setPower(leftRubbPower);
        robo.rightDrive.setPower(rightRubbPower);
        // endregion

        // region Intake + Moving Servo intake  (gp1)
        if(gamepad1.right_bumper){
            robo.leftIntake.setPower(robo.INTAKE_POWER);
            robo.rightIntake.setPower(robo.INTAKE_POWER);           // standard mode: sucks in cubes
        } else if (gamepad1.b) {
            robo.leftIntake.setPower(-robo.INTAKE_POWER);           // reverse mode: reverses intake
            robo.rightIntake.setPower(-robo.INTAKE_POWER);
        } else {
            robo.leftIntake.setPower(0);                            // default mode: OFF
            robo.rightIntake.setPower(0);
        }


        // endregion

        // region Solar Panel/ Power line cube grabber + lift   (gp1)

        // solar panel lift controls
        double solarLiftPower = Range.clip(gamepad1.right_stick_y, -1.0, 1.0);

        // solar panel motion controls
        if(solarLiftPower > 0){
            robo.solarLift.setPosition(robo.CONTINUOUS_SERVO_ANTI_CLOCKWISE);
        } else if(solarLiftPower < 0){
            robo.solarLift.setPosition(robo.CONTINUOUS_SERVO_CLOCKWISE);
        } else {
            robo.solarLift.setPosition(robo.CONTINUOUS_SERVO_STOP);
        }


        // solar panel grab controls
        if(gamepad1.x){
            robo.solarGrabber.setPosition(robo.CONTINUOUS_SERVO_ANTI_CLOCKWISE);     // close grip
        } else if (gamepad1.y) {
            robo.solarGrabber.setPosition(robo.CONTINUOUS_SERVO_CLOCKWISE);     // open grip
        } else{
            robo.solarGrabber.setPosition(robo.CONTINUOUS_SERVO_STOP);     // keep grip same
        }
        // endregion

        // region lift platform which lifts cubes     (gp2)
        double right_stick = Range.clip(gamepad2.right_stick_y, -1.0, 1.0);

        if(gamepad2.right_bumper) {
            robo.leftLift.setPower(0.5);
            robo.rightLift.setPower(0.5);
        } else{
            if (-right_stick != 0) {
                robo.leftLift.setPower(-right_stick);
                robo.rightLift.setPower(-right_stick);
            }
        }
        // endregion

        // region pushing cubes (linear motion) (gp2)
            double cubePushPower = Range.clip(gamepad2.left_stick_y, -1.0, 1.0);
            if (-cubePushPower > 0.3){
                robo.pushingWall.setPower(0.3);
            } else if (-cubePushPower < -0.3){
                robo.pushingWall.setPower(-0.3);
            } else {
                robo.pushingWall.setPower(-cubePushPower);
            }

        // endregion

        //region Rubber Servo    (gp1)

        if(gamepad1.dpad_right){
            robo.rubberCoiler.setPosition(robo.CONTINUOUS_SERVO_CLOCKWISE);
        }
        else if(gamepad1.dpad_left){
            robo.rubberCoiler.setPosition(robo.CONTINUOUS_SERVO_ANTI_CLOCKWISE);
        }
        else {
            robo.rubberCoiler.setPosition(robo.CONTINUOUS_SERVO_STOP);
        }
        //endregion

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors Driving", "left (%.2f), right (%.2f)", leftRubbPower, rightRubbPower);
        telemetry.addData("Motors Intake", "left (%.2f), right (%.2f)", robo.leftIntake.getPower(), robo.rightIntake.getPower());
        telemetry.addData("Motor Lifts", "left (%.2f), right (%.2f)", robo.leftLift.getPower(), robo.rightLift.getPower());


    /*
     * Code to run ONCE after the driver hits STOP
     */
    }
    @Override
    public void stop() {
    }

    private boolean servoIsBusy(double currentPos, double targetPos) { // used to play commands ONCE servo is in place
        boolean reachedPosition;

        if(currentPos != targetPos){
            reachedPosition = false;
        } else{
            reachedPosition = true;
        }
        return reachedPosition;
    } // used to play commands ONCE servo is in place



    /*
     * Code below makes the robot move in a right-angled triangle
     */
    private void triangularMotion (double speed, double backingDistanceCm, double turnAngle) {} // Not done

    /*
     * Code below uses the triangular motion method and loops it in a way to create a horizontal motion
     */
    private void strafe (double speed, double backingDistanceCm, double turnAngle){} // not done
}
