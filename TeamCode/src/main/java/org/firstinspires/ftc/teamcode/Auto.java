package org.firstinspires.ftc.teamcode;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/*
 * This OpMode is an example driver-controlled (TeleOp) mode for the goBILDA 2024-2025 FTC
 * Into The Deep Starter Robot
 * The code is structured as a LinearOpMode
 *
 * This robot has a two-motor differential-steered (sometimes called tank or skid steer) drivetrain.
 * With a left and right drive motor.
 * The drive on this robot is controlled in an "Arcade" style, with the left stick Y axis
 * controlling the forward movement and the right stick X axis controlling rotation.
 * This allows easy transition to a standard "First Person" control of a
 * mecanum or omnidirectional chassis.
 *
 * The drive wheels are 96mm diameter traction (Rhino) or omni wheels.
 * They are driven by 2x 5203-2402-0019 312RPM Yellow Jacket Planetary Gear-motors.
 *
 * This robot's main scoring mechanism includes an arm powered by a motor, a "wrist" driven
 * by a servo, and an intake driven by a continuous rotation servo.
 *
 * The arm is powered by a 5203-2402-0051 (50.9:1 Yellow Jacket Planetary Gearmotor) with an
 * external 5:1 reduction. This creates a total ~254.47:1 reduction.
 * This OpMode uses the motor's encoder and the RunToPosition method to drive the arm to
 * specific setpoints. These are defined as a number of degrees of rotation away from the arm's
 * starting position.
 *
 * Make super sure that the arm is reset into the robot, and the wrist is folded in before
 * you run start the OpMode. The motor's encoder is "relative" and will move the number of degrees
 * you request it to based on the starting position. So if it starts too high, all the motor
 * setpoints will be wrong.
 *
 * The wrist is powered by a goBILDA Torque Servo (2000-0025-0002).
 *
 * The intake wheels are powered by a goBILDA Speed Servo (2000-0025-0003) in Continuous Rotation mode.
 */


@Autonomous(name="Auto", group="Robot")
//@Disabled
public class Auto extends LinearOpMode {

    /* Declare OpMode members. */
    public DcMotor leftDriveF  = null; //the left drivetrain motor
    public DcMotor rightDriveF = null; //the right drivetrain motor
    public DcMotor leftDriveR  = null; //the left drivetrain motor
    public DcMotor rightDriveR = null; //the right drivetrain motor
    public DcMotor armMotor    = null; //the arm motor
    public DcMotor extendMotor = null; // extender motor
    public CRServo intake      = null; //the active intake servo
    public Servo   tilt_left   = null; //the tilt left servo        // REVERSE
    public Servo   tilt_right  = null; // tilt right servo
    public Servo   wrist      = null; // rotating servo arm


    /* This constant is the number of encoder ticks for each degree of rotation of the arm.
    To find this, we first need to consider the total gear reduction powering our arm.
    First, we have an external 20t:100t (5:1) reduction created by two spur gears.
    But we also have an internal gear reduction in our motor.
    The motor we use for this arm is a 117RPM Yellow Jacket. Which has an internal gear
    reduction of ~50.9:1. (more precisely it is 250047/4913:1)
    We can multiply these two ratios together to get our final reduction of ~254.47:1.
    The motor's encoder counts 28 times per rotation. So in total you should see about 7125.16
    counts per rotation of the arm. We divide that by 360 to get the counts per degree. */
    final double ARM_TICKS_PER_DEGREE =
            28 // number of encoder ticks per rotation of the bare motor
                    * 250047.0 / 4913.0 // This is the exact gear ratio of the 50.9:1 Yellow Jacket gearbox
                    * 100.0 / 20.0 // This is the external gear reduction, a 20T pinion gear that drives a 100T hub-mount gear
                    * 1/360.0; // we want ticks per degree, not per rotation

    /** @noinspection unused*/
    final double EXTEND_TICKS_PER_DEGREE =
            28 //encoder ticks
                    *250047.0/4913.0 // exact ratio
                    *1/360.0; // ticks per degree


    final double ROTATION_TICKS_PER_DEGREE =
            28 //encoder ticks
                    *3591.0/187.0 // exact ratio
                    *1/360.0
                    *180.0/PI
                    *10.0/48.0; // ticks per degree
    /* These constants hold the position that the arm is commanded to run to.
    These are relative to where the arm was located when you start the OpMode. So make sure the
    arm is reset to collapsed inside the robot before you start the program.

    In these variables you'll see a number in degrees, multiplied by the ticks per degree of the arm.
    This results in the number of encoder ticks the arm needs to move in order to achieve the ideal
    set position of the arm. For example, the ARM_SCORE_SAMPLE_IN_LOW is set to
    160 * ARM_TICKS_PER_DEGREE. This asks the arm to move 160° from the starting position.
    If you'd like it to move further, increase that number. If you'd like it to not move
    as far from the starting position, decrease it. */

//    final double ARM_COLLAPSED_INTO_ROBOT  = 0;
    final double ARM_COLLECT               = 173 * ARM_TICKS_PER_DEGREE;
    final double ARM_SCORING_POS           = 90  * ARM_TICKS_PER_DEGREE;

//    final double ARM_WINCH_ROBOT           = 20  * ARM_TICKS_PER_DEGREE;
    final double ARM_STARTING_CONFIG       = 50  * ARM_TICKS_PER_DEGREE;

    // These constants will be used for extending the arm after some fine-tuning
//    final double RETRACTED_ARM = 0;
//    final double EXTENDED_ARM = 8000 * EXTEND_TICKS_PER_DEGREE; // experimental value TBD

    /* Variables to store the speed the intake servo should be set at to intake, and deposit game elements. */
    final double INTAKE_COLLECT    = -1.0;
    final double INTAKE_OFF        =  0.0;
    final double INTAKE_DEPOSIT    =  0.5;

    /* Variables to store the positions that the wrist should be set to when folding in, or folding out. */
//    final double WRIST_FOLDED_IN   = 0;
    final double WRIST_FOLDED_OUT  = 0.68;
//    final double WRIST_HIGH_CHAMBER = .34;
    final  double STARTING_CONFIG = .65;
    final double COLLECTING_POSITION = .1;
//    final double SCORING_POSITION = .34;

    /* A number in degrees that the triggers can adjust the arm position by */

    /* Variables that are used to set the arm to a specific position */
//    double armPosition = (int)ARM_COLLAPSED_INTO_ROBOT;

//    double extendPosition = (int)RETRACTED_ARM;
    double read_pos;
    double func;
//    double read_extender_pos;

    /** @noinspection SameParameterValue*/
    private void Move_Distance_cm(double left_forward, double left_rear, double right_forward, double right_rear, double power)
    {

        ((DcMotorEx) leftDriveF).setMotorEnable();
        ((DcMotorEx) leftDriveR).setMotorEnable();
        ((DcMotorEx) rightDriveF).setMotorEnable();
        ((DcMotorEx) rightDriveR).setMotorEnable();
        telemetry.addLine("Enabled");
        telemetry.update();

        leftDriveF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftDriveR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDriveF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDriveR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        telemetry.addLine("RESET");
        telemetry.update();

        int left_F = (int) (-1*left_forward*ROTATION_TICKS_PER_DEGREE);
        int left_R = (int) (-1*left_rear*ROTATION_TICKS_PER_DEGREE);
        int right_F = (int) (-1*right_forward*ROTATION_TICKS_PER_DEGREE);
        int right_R = (int) (-1*right_rear*ROTATION_TICKS_PER_DEGREE);

        leftDriveF.setTargetPosition(left_F);
        leftDriveR.setTargetPosition(left_R);
        rightDriveF.setTargetPosition(right_F);
        rightDriveR.setTargetPosition(right_R);

        telemetry.addLine("SET TARGET");
        telemetry.update();
        do
        {
            ((DcMotorEx) leftDriveF).setVelocity(power);
            ((DcMotorEx) leftDriveR).setVelocity(power);
            ((DcMotorEx) rightDriveF).setVelocity(power);
            ((DcMotorEx) rightDriveR).setVelocity(power);

            leftDriveF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftDriveR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDriveF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDriveR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            telemetry.addData("LEFT  F degrees: ", leftDriveF.getCurrentPosition()/ROTATION_TICKS_PER_DEGREE);
            telemetry.addData("LEFT R degrees: ", leftDriveR.getCurrentPosition()/ROTATION_TICKS_PER_DEGREE);
            telemetry.addData("RIGHT F degrees: ", rightDriveF.getCurrentPosition()/ROTATION_TICKS_PER_DEGREE);
            telemetry.addData("RIGHT R degrees: ", rightDriveR.getCurrentPosition()/ROTATION_TICKS_PER_DEGREE);
            telemetry.update();
        }
        while (rightDriveR.isBusy());
        ((DcMotorEx) leftDriveF).setMotorDisable();
        ((DcMotorEx) leftDriveR).setMotorDisable();
        ((DcMotorEx) rightDriveF).setMotorDisable();
        ((DcMotorEx) rightDriveR).setMotorDisable();

        telemetry.addLine("End of Sleep");
        telemetry.update();
    }

    /** @noinspection SameParameterValue*/
    private void Arm_handler(double Target_Position)
    {
        armMotor.setTargetPosition((int) Target_Position);
        do
        {
            if ( armMotor.getCurrentPosition() < 90) {
                read_pos = armMotor.getCurrentPosition() / ARM_TICKS_PER_DEGREE;
                func = 1100 * cos(PI * read_pos / 90) + 1900;
                ((DcMotorEx) armMotor).setVelocity(func);
            }

            if (armMotor.getCurrentPosition() >= 90) {
                ((DcMotorEx) armMotor).setVelocity(800);
            }
            armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        while (armMotor.isBusy());
    }

    /** @noinspection SameParameterValue*/
    private void Tilt_servo_handler(double position)
    {
        tilt_left.setPosition(position);
        tilt_right.setPosition(position);

        while (tilt_right.getPosition() != position && tilt_left.getPosition() != position)
        {
            telemetry.addLine("sleep");
        }
        telemetry.update();
        telemetry.clear();
    }

    @Override
    public void runOpMode() {
        /*
        These variables are private to the OpMode, and are used to control the drivetrain.
         */


        /* Define and Initialize Motors */
        leftDriveF = hardwareMap.get(DcMotor.class, "left_front_drive"); //the left drivetrain motor //2C
        leftDriveR = hardwareMap.get(DcMotor.class, "left_rear_drive");//1C
        rightDriveF = hardwareMap.get(DcMotor.class, "right_front_drive");//the right drivetrain motor //3C
        rightDriveR = hardwareMap.get(DcMotor.class, "right_rear_drive");//0C
        armMotor   = hardwareMap.get(DcMotor.class, "left_arm"); //the arm motor  //1E
        extendMotor = hardwareMap.get(DcMotor.class, "extender"); // extender motor //0E

        /* Most skid-steer/differential drive robots require reversing one motor to drive forward.
        for this robot, we reverse the right motor.*/
        leftDriveF.setDirection(DcMotor.Direction.FORWARD);
        leftDriveR.setDirection(DcMotor.Direction.FORWARD);
        rightDriveF.setDirection(DcMotor.Direction.REVERSE);
        rightDriveR.setDirection(DcMotor.Direction.REVERSE);

        leftDriveF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftDriveR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDriveF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDriveR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /* Setting zeroPowerBehavior to BRAKE enables a "brake mode". This causes the motor to slow down
        much faster when it is coasting. This creates a much more controllable drivetrain. As the robot
        stops much quicker. */
        leftDriveF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftDriveR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDriveF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDriveR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /*This sets the maximum current that the control hub will apply to the arm before throwing a flag */
        ((DcMotorEx) armMotor).setCurrentAlert(5,CurrentUnit.AMPS);
        ((DcMotorEx) extendMotor).setCurrentAlert(5,CurrentUnit.AMPS);

        /* Before starting the armMotor. We'll make sure the TargetPosition is set to 0.
        Then we'll set the RunMode to RUN_TO_POSITION. And we'll ask it to stop and reset encoder.
        If you do not have the encoder plugged into this motor, it will not run in this code. */
        armMotor.setTargetPosition(0);
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /* Same as previous code block, but for the extendMotor */

        extendMotor.setTargetPosition(0);
        extendMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        extendMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        /* Define and initialize servos.*/
        intake = hardwareMap.get(CRServo.class, "intake");
        tilt_left  = hardwareMap.get(Servo.class, "tilt_left");
        tilt_right = hardwareMap.get(Servo.class, "tilt_right");
        wrist = hardwareMap.get(Servo.class, "wrist");

        tilt_left.setDirection(Servo.Direction.REVERSE);
        wrist.setDirection(Servo.Direction.REVERSE);

        /* Make sure that the intake is off, and the wrist is folded in. */
        intake.setPower(INTAKE_OFF);

        Tilt_servo_handler(STARTING_CONFIG);
        wrist.setPosition(WRIST_FOLDED_OUT);
        Arm_handler(ARM_STARTING_CONFIG);

        /* Send telemetry message to signify robot waiting */
        telemetry.addLine("Robot Ready.");
        telemetry.update();

        /* Wait for the game driver to press play */
        waitForStart();

        /* Run until the driver presses stop */
        if (opModeIsActive()) {


            Move_Distance_cm(100, 100, 100, 100, 800);

            Move_Distance_cm(50, -50, -50, 50, 800);

            Arm_handler(ARM_COLLECT);
            Tilt_servo_handler(COLLECTING_POSITION);
            intake.setPower(INTAKE_COLLECT);

            Move_Distance_cm(50, 50, 50, 50, 800);

            Arm_handler(ARM_SCORING_POS);

            Move_Distance_cm(50, 50, 50, 50, 800);

            Move_Distance_cm(50, 50, -50, -50, 800);  //rotation of robot without new method

            Move_Distance_cm(50, 50, 50, 50, 800);

            Arm_handler(ARM_COLLECT);
            intake.setPower(INTAKE_DEPOSIT);

            Arm_handler(ARM_SCORING_POS);
            intake.setPower(INTAKE_OFF);

            Move_Distance_cm(50, 50, -50, -50, 800);  //rotation of robot without new method

            Move_Distance_cm(50, 50, 50, 50, 800);

            Arm_handler(ARM_COLLECT);
            Tilt_servo_handler(COLLECTING_POSITION);
            intake.setPower(INTAKE_COLLECT);

            Move_Distance_cm(50, 50, 50, 50, 800);

            Arm_handler(ARM_SCORING_POS);

            Move_Distance_cm(50, 50, -50, -50, 800);  //rotation of robot without new method

            Move_Distance_cm(50, 50, 50, 50, 800);

            Arm_handler(ARM_COLLECT);
            intake.setPower(INTAKE_DEPOSIT);

            Arm_handler(ARM_SCORING_POS);
            intake.setPower(INTAKE_OFF);


            /* Here we handle the three buttons that have direct control of the intake speed.
            These control the continuous rotation servo that pulls elements into the robot,
            If the user presses X, it sets the intake power to the final variable that
            holds the speed we want to collect at.
            If the user presses B, it sets the servo to Off.
            And if the user presses A it reveres the servo to spit out the element.

            reminder:Y additional button*/


            /* TECH TIP: If Else statements:
            We're using an else if statement on "gamepad2.x" and "gamepad2.b" just in case
            multiple buttons are pressed at the same time. If the driver presses both "b" and "x"
            at the same time. "x" will win over and the intake will turn on. If we just had
            three if statements, then it will set the intake servo's power to multiple speeds in
            one cycle. Which can cause strange behavior. */


            /* Here we implement a set of if else statements to set our arm to different scoring positions.
            We check to see if a specific button is pressed, and then move the arm (and sometimes
            intake and wrist) to match. For example, if we click the right bumper we want the robot
            to start collecting. So it moves the armPosition to the ARM_COLLECT position,
            it folds out the wrist to make sure it is in the correct orientation to intake, and it
            turns the intake on to the COLLECT mode.*/



            // adding extending functionality to the joysticks of gamepad2

            /* Here we create a "fudge factor" for the arm position.
            This allows you to adjust (or "fudge") the arm position slightly with the gamepad triggers.
            We want the left trigger to move the arm up, and right trigger to move the arm down.
            So we add the right trigger's variable to the inverse of the left trigger. If you pull
            both triggers an equal amount, they cancel and leave the arm at zero. But if one is larger
            than the other, it "wins out". This variable is then multiplied by our FUDGE_FACTOR.
            The FUDGE_FACTOR is the number of degrees that we can adjust the arm by with this function. */

            //armPositionFudgeFactor = FUDGE_FACTOR * (gamepad2.right_trigger + (-gamepad2.left_trigger));

            /* TECH TIP: Encoders, integers, and doubles
            Encoders report when the motor has moved a specified angle. They send out pulses which
            only occur at specific intervals (see our ARM_TICKS_PER_DEGREE). This means that the
            position our arm is currently at can be expressed as a whole number of encoder "ticks".
            The encoder will never report a partial number of ticks. So we can store the position in
            an integer (or int).
            A lot of the variables we use in FTC are doubles. These can capture fractions of whole
            numbers. Which is great when we want our arm to move to 122.5°, or we want to set our
            servo power to 0.5.

            setTargetPosition is expecting a number of encoder ticks to drive to. Since encoder
            ticks are always whole numbers, it expects an int. But we want to think about our
            arm position in degrees. And we'd like to be able to set it to fractions of a degree.
            So we make our arm positions Doubles. This allows us to precisely multiply together
            armPosition and our armPositionFudgeFactor. But once we're done multiplying these
            variables. We can decide which exact encoder tick we want our motor to go to. We do
            this by "typecasting" our double, into an int. This takes our fractional double and
            rounds it to the nearest whole number.
            */

            /* Check to see if our arm is over the current limit, and report via telemetry. */
//            if (((DcMotorEx) armMotor).isOverCurrent())
//            {
//                telemetry.addLine("ARM MOTOR EXCEEDED CURRENT LIMIT!");
//            }
//            if (((DcMotorEx) extendMotor).isOverCurrent())
//            {
//                telemetry.addLine("EXTEND MOTOR EXCEEDED CURRENT LIMIT!");
//            }
//
//
//            /* send telemetry to the driver of the arm's current position and target position */
//            telemetry.addData("armTarget degrees: ", armMotor.getTargetPosition()/ARM_TICKS_PER_DEGREE);
//            telemetry.addData("arm Encoder degrees: ", armMotor.getCurrentPosition()/ARM_TICKS_PER_DEGREE);
//            telemetry.update();

        }
    }
}
