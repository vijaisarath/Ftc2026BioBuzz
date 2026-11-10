/* Copyright (c) 2025 FIRST. All rights reserved.
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

import static org.firstinspires.ftc.teamcode.HardwareTNCreator.LIMIT_TICKS;
import static org.firstinspires.ftc.teamcode.HardwareTNCreator.MOTOR_POWER;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/*
 * This OpMode illustrates how to program your robot to drive field relative.  This means
 * that the robot drives the direction you push the joystick regardless of the current orientation
 * of the robot.
 *
 * This OpMode assumes that you have four mecanum wheels each on its own motor named:
 *   front_left_motor, front_right_motor, back_left_motor, back_right_motor
 *
 *   and that the left motors are flipped such that when they turn clockwise the wheel moves backwards
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 *
 */
@TeleOp(name = "Manual Drive ODO", group = "Robot")
//@Disabled
public class MecanumFieldOdoRelativeDrive extends LinearOpMode {
    // This declares the four motors needed
    double theta;
    // declare motor speed variables
    double FR;
    double FL;
    double BR;
    double BL;

    int start = 0;
    private final ElapsedTime runtime = new ElapsedTime();
    HardwareTNCreator myRobot = new HardwareTNCreator();

    // This declares the IMU needed to get the current direction the robot is facing
    // This needs to be changed to match the orientation on your robot
    RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
            RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
    RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD;
    RevHubOrientationOnRobot orientationOnRobot = new
            RevHubOrientationOnRobot(logoDirection, usbDirection);
    private int centerPosition = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        myRobot.init(hardwareMap);

        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        myRobot.armServo.scaleRange(0.25, 0.6);
        myRobot.armServo.setPosition(0.0);
        myRobot.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        centerPosition = myRobot.motor.getCurrentPosition();

        boolean previousA = false;
        boolean previousB = false;

        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            if (gamepad1.a) {
                moveArm(0.1, -75);
               //myRobot.motor.setPower(0);
            }
            if (gamepad1.b) {
                moveArm(0.1, -10);
                //myRobot.motor.setPower(0);
            }

            if (start < 1) {
                sleep(200);
                myRobot.odo.resetPosAndIMU();
                telemetry.addLine("Reset completed");
                telemetry.update();
                sleep(200);
                start++;
            }

            if (gamepad1.x) {
                myRobot.armmotor.setPower(0.5);
                sleep(950);
                myRobot.armmotor.setPower(0);
            } else if (gamepad1.y) {
                myRobot.armmotor.setPower(-0.5);
                sleep(950);
                myRobot.armmotor.setPower(0);
            }

            if (gamepad1.dpad_up) {
                myRobot.clawServo.setPosition(1.0);
            } else if (gamepad1.dpad_down) {
                myRobot.clawServo.setPosition(0.0);
            }

//            drive(-gamepad1.right_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
            driveFieldRelative(-gamepad1.right_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
            //GAMEPAD 2
            // ON/OFF shooter
            if (gamepad2.x) {
                myRobot.leftIntake_srv.setPower(-1.0);
                myRobot.rightIntake_srv.setPower(-1.0);
                myRobot.pusher_srv.setPower(-1.0);
                myRobot.intakeMotor.setPower(1.0);
            } else if (gamepad2.y) {
                //myRobot.leftShooter.setPower(0);
                myRobot.leftIntake_srv.setPower(0);
                myRobot.rightIntake_srv.setPower(0);
                myRobot.intakeMotor.setPower(0);
                myRobot.pusher_srv.setPower(0.0);
            }

            // ON/OFF intake wheel
            if (gamepad2.a) {
                myRobot.shooterMotor.setPower(0.5);

            } else if (gamepad2.b) {
                myRobot.shooterMotor.setPower(0);

            }

            telemetry.addData("FL | FR", "%.3f | %.3f", FL, FR);
            telemetry.addData("BL | BR", "%.3f | %.3f", BL, BR);

            telemetry.addData("Ball Shooter", "%.3f", myRobot.shooterMotor.getPower());

            telemetry.addData("============","");
            telemetry.addData("Intake motor", "%.3f", myRobot.intakeMotor.getPower());
            telemetry.addData("Servo Push", "%.3f", myRobot.pusher_srv.getPower());
            telemetry.addData("Servo Intake", "%.3f", myRobot.leftIntake_srv.getPower());

            telemetry.addData("============","");

            telemetry.addData("Armmotor", "%.3f", myRobot.armmotor.getPower());
            telemetry.addData("Claw pos", "%.3f", myRobot.clawServo.getPosition());
            telemetry.addData("Arm pos", "%.3f", myRobot.armServo.getPosition());
            telemetry.addData("Arm C->T position: ", "%4d -> %4d", myRobot.motor.getCurrentPosition(), myRobot.motor.getTargetPosition());

            //Verify ODO setting
            // Read the latest data from Pinpoint
            myRobot.odo.update();

            // Position
//            telemetry.addData("X Position (mm)", myRobot.odo.getPosX(DistanceUnit.CM));
//            telemetry.addData("Y Position (mm)", myRobot.odo.getPosY(DistanceUnit.CM));
//
//            // Heading
//            telemetry.addData("Heading (degrees)",
//                    Math.toDegrees(myRobot.odo.getHeading(AngleUnit.RADIANS)));

//            // Raw encoder readings
//            telemetry.addData("X Encoder", myRobot.odo.getEncoderX());
//            telemetry.addData("Y Encoder", myRobot.odo.getEncoderY());
//
//            // Device performance
//            telemetry.addData("Frequency (Hz)", myRobot.odo.getFrequency());
//            telemetry.addData("Loop Time (us)", myRobot.odo.getLoopTime());
            telemetry.update();
            idle();
        }

        if (isStopRequested()) {
            myRobot.frontLeftMotor.setPower(0);
            myRobot.frontRightMotor.setPower(0);
            myRobot.backLeftMotor.setPower(0);
            myRobot.backRightMotor.setPower(0);

            myRobot.shooterMotor.setPower(0);
            myRobot.intakeMotor.setPower(0);
        }
    }

    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate

        double frontLeftPower = forward + right + rotate;
        double backLeftPower = forward - right + rotate;

        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;

        double maxPower = myRobot.MOTOR_MAX_MANUAL;
        double maxSpeed = myRobot.MOTOR_MAX_MANUAL;  // make this slower for outreaches

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.

        FL = (maxSpeed * (frontLeftPower / maxPower));
        FR = (maxSpeed * (frontRightPower / maxPower));
        BL = (maxSpeed * (backLeftPower / maxPower));
        BR = (maxSpeed * (backRightPower / maxPower));

        myRobot.frontLeftMotor.setPower(FL);
        myRobot.frontRightMotor.setPower(FR);
        myRobot.backLeftMotor.setPower(BL);
        myRobot.backRightMotor.setPower(BR);
    }

    // This routine drives the robot field relative
    private void driveFieldRelative(double forward, double right, double rotate) {
        // First, convert direction being asked to drive to polar coordinates
        theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        myRobot.odo.update(GoBildaPinpointDriver.ReadData.ONLY_UPDATE_HEADING);
        double heading = myRobot.odo.getHeading(AngleUnit.RADIANS);

        // Second, rotate angle by the angle the robot is pointing
        theta = AngleUnit.normalizeRadians(theta - heading);

        // Third, convert back to cartesian
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    public void moveArm(double speed, int ticks) {
        ticks = Range.clip(ticks, -100, 0);

        if (opModeIsActive()) {
            myRobot.motor.setTargetPosition(ticks);
            myRobot.motor.setPower(Math.abs(speed));
            myRobot.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            myRobot.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        if (myRobot.motor.isBusy()) {
            telemetry.addData("Status", "In ARM Motor");
        }
    }
}
