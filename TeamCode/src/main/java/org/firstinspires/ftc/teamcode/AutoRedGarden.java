package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Autonomous Red Garden", group = "Linear Opmode")
// @Disabled
public class AutoRedGarden extends LinearOpMode {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    HardwareTNCreator myRobot = new HardwareTNCreator();   // Use a Pushbot's hardware

    // declare motor speed variables
    int FR, FL, BR, BL;

    // operational constants
    double motorMax = myRobot.MOTOR_MAX_AUTO;

    static final double COUNTS_PER_MOTOR_REV = 537.7;  // 19.2:1 goBILDA Yellow Jacket
    static final double DRIVE_GEAR_REDUCTION = 1.0;    // direct drive
    static final double WHEEL_DIAMETER_INCHES = 3.78;   // 100mm mecanum wheel

    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);


    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        myRobot.init(hardwareMap);

        myRobot.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myRobot.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myRobot.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myRobot.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Reset speed variables
        FL = 0;
        FR = 0;
        BL = 0;
        BR = 0;

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Red1", "Starting at %7d : %7d : %7d : %7d",
                myRobot.frontLeftMotor.getCurrentPosition(),
                myRobot.frontRightMotor.getCurrentPosition(),
                myRobot.backLeftMotor.getCurrentPosition(),
                myRobot.backRightMotor.getCurrentPosition()
        );
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        double speed = 0.62;
        double speed_intake = 0.4;

        //moveBack(0.40, 3.00f);
        //moveFront(0.40,3.00f);
//        rotateClockWise(0.10, 40.0f);
//        rotateAntiClockWise(0.1, 40.0f);
//        shooterMotorON();
//        shooterMotorOFF();


        moveFront(0.40,3.00f);
        sleep(200);

        shooterMotorON();
        sleep(3000);

        shooterMotorOFF();
        moveRight(0.4,3.0f);

        stopRobot();
        idle();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
            idle();
        }
        if (isStopRequested()) {
            stopRobot();
        }
    }


    private void intakeClockwise() {
        myRobot.intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        myRobot.intakeMotor.setPower(myRobot.INTAKE_SPEED);
    }

    private void intakeAntiClockwise() {
        myRobot.intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        myRobot.intakeMotor.setPower(myRobot.INTAKE_SPEED);
    }

    private void intakeStop() {
        myRobot.intakeMotor.setPower(0);
    }


    private void shooterMotorON() {
        myRobot.shooterMotor.setPower(myRobot.SHOOTER_MAX_AUTO);
        myRobot.pusher_srv.setPower(myRobot.PUSHERSRV_MAX_AUTO);
        sendTelemetryData("ShooterMotorRightON");
    }

    private void shooterMotorOFF() {
        myRobot.shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        myRobot.shooterMotor.setPower(0);
        myRobot.pusher_srv.setPower(0);
        sendTelemetryData("ShooterMotorRightOFF");
    }

    private void servoOpen() {
        myRobot.clawServo.setPosition(0.0);
    }

    private void servoClose() {
        myRobot.clawServo.setPosition(1.0);
    }

    /// region Robot Wheel code
    private void stopRobot() {
        FL = BL = FR = BR = 0;
        assignPowerToMotors(0);
        sendTelemetryData("stopRobot");
    }

    private void moveFront(int power) {
        FL = power;
        BL = power;
        FR = power;
        BR = power;
        assignPowerToMotors(power);
        sendTelemetryData("moveFront");
    }

    private void moveFront(double power, float distanceInInch) {
        int distTick = (int) (distanceInInch * COUNTS_PER_INCH);
        FL = BL = FR = BR = distTick;
        setTargetToMotors(power, "moveFront");
    }

    private void moveBack(double power, float distanceInInch) {
        int distTick = (int) (distanceInInch * COUNTS_PER_INCH);
        FL = BL = FR = BR = -distTick;
        setTargetToMotors(power, "moveBack");
    }

    private void moveRight(double power, float distanceInInch) {
        int distTick = (int) (distanceInInch * COUNTS_PER_INCH);
        FL = +distTick;
        BL = -distTick;

        FR = -distTick;
        BR = +distTick;
        setTargetToMotors(power, "moveRight");
    }

    private void moveLeft(double power, float distanceInInch) {
        int distTick = (int) (distanceInInch * COUNTS_PER_INCH);
        FL = -distTick;
        BL = +distTick;

        FR = +distTick;
        BR = -distTick;

        setTargetToMotors(power, "moveLeft");
    }

    //Pivot Movements
    private void moveFrontLeft(int power, float distanceInInch) {
        FL = BR = 0;
        BL = FR = power;
        setTargetToMotors(distanceInInch, "moveFrontLeft");
    }

    private void moveBackLeft(int power, float distanceInInch) {
        FL = BR = 0;
        BL = FR = -power;
        setTargetToMotors(distanceInInch, "moveBackLeft");
    }

    private void moveBackRight(int power, float distanceInInch) {
        FL = BR = -power;
        BL = FR = 0;
        setTargetToMotors(distanceInInch, "moveBackRight");
    }

    private void moveFrontRight(int power, float distanceInInch) {
        FL = BR = power;
        BL = FR = 0;
        setTargetToMotors(distanceInInch, "moveFrontRight");
    }

    private void rotateAntiClockWise(double power, float distanceInInch) {
        int distTick = (int) (distanceInInch * COUNTS_PER_INCH);
        BR = FR = distTick;
        BL = FL = -distTick;
        setTargetToMotors(power, "rotateAntiClockWise");
    }

    private void rotateClockWise(double power, float distanceInInch) {
        int distTick = (int) (distanceInInch * COUNTS_PER_INCH);
        FL = distTick;
        FR = -distTick;
        BL = distTick;
        BR = -distTick;
        setTargetToMotors(power, "rotateClockWise");
    }

    private void assignPowerToMotors(double power) {
        // Clip motor power values to +-motorMax
//        FL = Math.max(-motorMax, Math.min(FL, motorMax));
//        FR = Math.max(-motorMax, Math.min(FR, motorMax));
//        BL = Math.max(-motorMax, Math.min(BL, motorMax));
//        BR = Math.max(-motorMax, Math.min(BR, motorMax));

        power = Math.max(-motorMax, Math.min(power, motorMax));

        // Send values to the motors
        myRobot.frontLeftMotor.setPower(power);
        myRobot.frontRightMotor.setPower(power);
        myRobot.backLeftMotor.setPower(power);
        myRobot.backRightMotor.setPower(power);
    }

    private void setTargetToMotors(double power, String commandName) {
        myRobot.frontLeftMotor.setTargetPosition(FL);
        myRobot.frontRightMotor.setTargetPosition(FR);
        myRobot.backLeftMotor.setTargetPosition(BL);
        myRobot.backRightMotor.setTargetPosition(BR);

        myRobot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        myRobot.frontRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        myRobot.backLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        myRobot.backRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        assignPowerToMotors(power);

        while (opModeIsActive() &&
                (myRobot.frontLeftMotor.isBusy() && myRobot.frontRightMotor.isBusy() && myRobot.backRightMotor.isBusy() && myRobot.backLeftMotor.isBusy()))
        // (myRobot.frontLeftMotor.isBusy() || myRobot.frontRightMotor.isBusy() || myRobot.backRightMotor.isBusy() || myRobot.backLeftMotor.isBusy()))
        {
            sendTelemetryData(commandName);
        }

        stopRobot();

        myRobot.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myRobot.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myRobot.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myRobot.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        myRobot.frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        myRobot.frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        myRobot.backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        myRobot.backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    //endregion

    private void sendTelemetryData(String param) {
        telemetry.addData("Movement", param);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData(param, "Current post %7d : %7d : %7d : %7d",
                myRobot.frontLeftMotor.getCurrentPosition(),
                myRobot.frontRightMotor.getCurrentPosition(),
                myRobot.backLeftMotor.getCurrentPosition(),
                myRobot.backRightMotor.getCurrentPosition()
        );
        telemetry.addData(param, "Target post %7d : %7d : %7d : %7d",
                myRobot.frontLeftMotor.getTargetPosition(),
                myRobot.frontRightMotor.getTargetPosition(),
                myRobot.backLeftMotor.getTargetPosition(),
                myRobot.backRightMotor.getTargetPosition()
        );

        telemetry.addData("FL Wheel Pwr :", myRobot.frontLeftMotor.getPower());


        telemetry.addData("Intake Pwr :", myRobot.intakeMotor.getPower());
        telemetry.addData("Intake Dir :", myRobot.intakeMotor.getDirection());

        telemetry.addData("Shooter Pwr R | L :", "%f3 | %f3 ", myRobot.rightIntake_srv.getPower(), myRobot.leftIntake_srv.getPower());
        telemetry.addData("Shooter R Dir :", myRobot.pusher_srv.getDirection());
        telemetry.addData("Shooter L Dir :", myRobot.shooterMotor.getDirection());

        //  telemetry.addData("Distance :", myRobot.distanceSensor.getDistance(DistanceUnit.CM));

        telemetry.addData("Wheel Max Power: ", myRobot.MOTOR_MAX_AUTO);
        telemetry.addData("Shooter Max Power: ", myRobot.SHOOTER_MAX_AUTO);
        telemetry.update();
    }

}
