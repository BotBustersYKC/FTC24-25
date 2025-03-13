package org.firstinspires.ftc.teamcode;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
@TeleOp(name="READ_POS_MOTOR", group="Robot")
public class read_pos extends LinearOpMode {
    public DcMotor leftDriveF = null; //the left drivetrain motor
    public DcMotor rightDriveF = null; //the right drivetrain motor
    public DcMotor leftDriveR = null; //the left drivetrain motor
    public DcMotor rightDriveR = null; //the right drivetrain motor
    final double ROTATION_TICKS_PER_DEGREE =
            28 //encoder ticks
                    * 3591.0 / 187.0 // exact ratio
                    * 1 / 360.0
                    * 180.0 / PI
                    * 10.0 / 48.0;

    @Override
    public void runOpMode()
    {
        leftDriveF = hardwareMap.get(DcMotor.class, "left_front_drive"); //the left drivetrain motor //2C
        leftDriveR = hardwareMap.get(DcMotor.class, "left_rear_drive");//1C
        rightDriveF = hardwareMap.get(DcMotor.class, "right_front_drive");//the right drivetrain motor //3C
        rightDriveR = hardwareMap.get(DcMotor.class, "right_rear_drive");

        leftDriveF.setDirection(DcMotor.Direction.REVERSE);
        leftDriveR.setDirection(DcMotor.Direction.REVERSE);
        rightDriveF.setDirection(DcMotor.Direction.FORWARD);
        rightDriveR.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();

        /* Run until the driver presses stop */
        while (opModeIsActive())
        {
            telemetry.addData("LEFT  F degrees: ", leftDriveF.getTargetPosition()/ROTATION_TICKS_PER_DEGREE);
            telemetry.addData("LEFT R degrees: ", leftDriveR.getCurrentPosition()/ROTATION_TICKS_PER_DEGREE);
            telemetry.addData("RIGHT F degrees: ", rightDriveF.getTargetPosition()/ROTATION_TICKS_PER_DEGREE);
            telemetry.addData("RIGHT R degrees: ", rightDriveR.getCurrentPosition()/ROTATION_TICKS_PER_DEGREE);
            telemetry.update();
        }

    }
}