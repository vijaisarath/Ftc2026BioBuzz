package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import android.util.Size;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;

import java.util.List;
import java.util.Locale;

@Autonomous(name = "Loading", group = "Linear Opmode")
//@Disabled
public class AutoRedLoading extends LinearOpMode {

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

    //Camera
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    private String detectedCluster = "NONE";
    private boolean blueClusterFound = false;
    private boolean scorableCell = false;
    private ElapsedTime timer = new ElapsedTime();

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
        telemetry.addData("BlueL", "Starting at %7d : %7d : %7d : %7d",
                myRobot.frontLeftMotor.getCurrentPosition(),
                myRobot.frontRightMotor.getCurrentPosition(),
                myRobot.backLeftMotor.getCurrentPosition(),
                myRobot.backRightMotor.getCurrentPosition()
        );
        telemetry.update();

        initAprilTag();

        while (!isStarted() && !isStopRequested()) {
            detectAprilTagCluster();
            sleep(20);
        }

        if (isStopRequested()) {
            visionPortal.close();
            return;
        }

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        moveFront(0.4, 2.00f);
        boolean tagFound = false;

        while (timer.seconds() < 17 && !tagFound) {
            telemetry.addLine("Waiting for Barcode detection! " + timer.seconds());
            telemetry.update();

            List<AprilTagDetection> detections = aprilTag.getDetections();

            for (AprilTagDetection detection : detections) {

                // Check whether this detection is a cluster
                if (detection instanceof AprilTagClusterDetection) {

                    AprilTagClusterDetection cluster = (AprilTagClusterDetection) detection;
                    String name = cluster.metadata.name.toLowerCase(Locale.US);
                    String shortName = cluster.metadata.shortName.toLowerCase(Locale.US);

                    telemetry.addData("Cluster Name", name);
                    telemetry.addData("Short Name", shortName);
                    telemetry.addData("X", cluster.ftcPose.x);
                    telemetry.addData("Y", cluster.ftcPose.y);
                    telemetry.addData("Yaw", cluster.ftcPose.yaw);
                    telemetry.addData("Camera-to-Tag Distance", "%.1f inches", cluster.ftcPose.range);
                    telemetry.addData("Visible Cluster %", cluster.percentClusterFound);

                    // Identify Blue Audience-side cluster
                    boolean isBlue = !name.isEmpty(); //name.contains("blue") || shortName.contains("blue");

                    boolean isAudience = !name.isEmpty(); //name.contains("audience") || shortName.contains("aud");

                    if (isBlue && isAudience) {
                        tagFound = true;
                        break;
                    }
                }
            }
        }
        visionPortal.close();

        if (tagFound) {
            telemetry.addLine("BLUE AUDIENCE CELL DETECTED!");
            telemetry.addLine("SHOOTER MOTOR STARTING");
            telemetry.update();
            shooterMotorON(1500);
            sleep(5000);
            shooterMotorOFF();
        } else {
            telemetry.addLine("NOOO BLUE AUDIENCE CELL DETECTED!");
            telemetry.update();
        }

        // moveBack(0.2, 2.0f);// for testing

        moveFront(0.4, 7.50f);
        moveRight(0.4, 45.0f);
        rotateAntiClockWise(0.6, 20.0f);

        stopRobot();
        idle();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
            idle();
        }
        if (isStopRequested()) {
            visionPortal.close();
            stopRobot();
            return;
        }
    }

    private void initAprilTag() {

        aprilTag = new AprilTagProcessor.Builder()
                .setLensIntrinsics(
                        545.584,
                        545.584,
                        350.588,
                        223.15
                )
                .build();

        aprilTag.setDecimation(1);

        VisionPortal.Builder builder = new VisionPortal.Builder();

        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam"));

        builder.setCameraResolution(new Size(640, 480));

        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);

        builder.addProcessor(aprilTag);

        visionPortal = builder.build();
    }

    private void detectAprilTagCluster() {

        detectedCluster = "NONE";
        blueClusterFound = false;
        scorableCell = false;

        List<AprilTagDetection> detections =
                aprilTag.getDetections();

        for (AprilTagDetection detection : detections) {

            if (detection instanceof AprilTagClusterDetection) {

                AprilTagClusterDetection cluster =
                        (AprilTagClusterDetection) detection;

                String clusterName = cluster.metadata.name;

                telemetry.addData("Cluster", clusterName);

                telemetry.addData(
                        "Visible Tags",
                        "%.1f%%",
                        cluster.percentClusterFound * 100
                );
//
//                telemetry.addData(
//                        "Range",
//                        "%.1f inches",
//                        cluster.ftcPose.range
//                );
//
//                telemetry.addData(
//                        "Bearing",
//                        "%.1f degrees",
//                        cluster.ftcPose.bearing
//                );
//
//                telemetry.addData(
//                        "Roll",
//                        "%.1f degrees",
//                        cluster.ftcPose.roll
//                );

                if (!clusterName.isEmpty()) {

                    blueClusterFound = true;
                    detectedCluster = clusterName;

                    // A scorable cell is normally right-side up
                    scorableCell = Math.abs(cluster.ftcPose.roll) < 90;
                    myRobot.rgbLight.setPosition(myRobot.RED);
                }
            }
        }

        telemetry.addData(
                "Blue Cluster Found",
                blueClusterFound
        );

        telemetry.addData(
                "Scorable Cell",
                scorableCell
        );
        telemetry.update();
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


    private void shooterMotorON(long sec) {
        myRobot.rgbLight.setPosition(myRobot.GREEN);
        myRobot.shooterMotor.setVelocity(myRobot.SHOOTER_targetVelocity_AUTO);
        sleep(sec);
        pulsePusher(6,500);
        sendTelemetryData("ShooterMotorRightON");
    }

    public void pulsePusher(int cycles, long delayMs) {
        for (int i = 0; i < cycles; i++) {
            myRobot.pusher_srv.setPower(myRobot.PUSHERSRV_MAX_AUTO);
            sleep(delayMs);
            myRobot.pusher_srv.setPower(1.0);
            sleep(delayMs);
        }
        myRobot.pusher_srv.setPower(myRobot.PUSHERSRV_MAX_AUTO);
    }

    private void shooterMotorOFF() {
        myRobot.shooterMotor.setVelocity(0);
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
        myRobot.rgbLight.setPosition(myRobot.GREEN);
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

        telemetry.addData("Shooter Pwr R | L :", "%.3f | %.3f ", myRobot.rightIntake_srv.getPower(), myRobot.leftIntake_srv.getPower());
        telemetry.addData("Shooter R Dir :", myRobot.pusher_srv.getDirection());
        telemetry.addData("Shooter L Dir :", myRobot.shooterMotor.getDirection());

        telemetry.addData("Ball Shooter", "%.3f -> %.3f", myRobot.SHOOTER_TARGET_RPM, (myRobot.shooterMotor.getVelocity() * 60.0 / myRobot.SHOOTER_TICKS_PER_REV));
        telemetry.update();
    }
}
