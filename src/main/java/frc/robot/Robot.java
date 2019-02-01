/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;// <-- Needed for xbox style controllers
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.*;// <-- gets us access to WPI_TalonSRX which works with wpilibj.drive.Mecanum
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.  It's been modifed to call the WPI_TalonSRX controllers, which use the can bus, 
 * instead of PWM.  
 */
public class Robot extends TimedRobot {
  // These will need to be updated to the CAN Ids of the WPI_TalonSRX's
  private static final int kFrontLeftChannel = 2;
  private static final int kRearLeftChannel = 3;
  private static final int kFrontRightChannel = 1;
  private static final int kRearRightChannel = 0;

  // What ever USB port we have the controller plugged into.
  private static final int kGamePadChannel = 0;

  //Lets map out the buttons
  private static final int kXboxButtonA = 1;
  private static final int kXboxButtonB = 2;
  private static final int kXboxButtonX = 3;
  private static final int kXboxButtonY = 4;

  private static final int kXboxButtonLB = 5; // <-- Left Button
  private static final int kXboxButtonRB = 6; // <-- Right Button
  private static final int kXboxButtonLT = 2; // <-- Left Trigger
  private static final int kXboxButtonRT = 3; // <-- Right Trigger

  private static final double kRampUpRate = 1.5; // The rate that the motor controller will speed up to full;
  private static final NeutralMode K_MODE = NeutralMode.Brake; // Setting the talons neutralmode to brake

  private MecanumDrive m_robotDrive;

  private GenericHID m_controllerDriver;

  private Gyro MyGyro;


  
  /**
   * This function if called when the robot boots up.
   * It creates the objects that are called by the other robot functions.
   */
  @Override
  public void robotInit() {

    WPI_TalonSRX frontLeftTalonSRX = new WPI_TalonSRX(kFrontLeftChannel);
    WPI_TalonSRX frontRightTalonSRX = new WPI_TalonSRX(kFrontRightChannel);
    WPI_TalonSRX rearLeftTalonSRX = new WPI_TalonSRX(kRearLeftChannel);
    WPI_TalonSRX rearRightTalonSRX = new WPI_TalonSRX(kRearRightChannel);


    m_robotDrive = new MecanumDrive(frontLeftTalonSRX, rearLeftTalonSRX, frontRightTalonSRX, rearRightTalonSRX);

    // m_controllerDriver = new Joystick(kJoystickChannel);
    
    m_controllerDriver = new XboxController(kGamePadChannel);

    MyGyro = new Gyro();//This should create a new Gyro object called MyGyro

    MyGyro.Init(); //Run the init method, to reset and calibrate the gyro.

    
    // Invert the left side motors.
    // You may need to change or remove this to match your robot.
    frontRightTalonSRX.setInverted(true);
    rearRightTalonSRX.setInverted(true);

    /**
     * Added to test out setting talon config some settings internal
     * to the TalonSRXs
     */
    frontRightTalonSRX.configOpenloopRamp(kRampUpRate);
    frontRightTalonSRX.setNeutralMode(K_MODE);

    frontLeftTalonSRX.configOpenloopRamp(kRampUpRate);
    frontLeftTalonSRX.setNeutralMode(K_MODE);
    
    rearRightTalonSRX.configOpenloopRamp(kRampUpRate);
    rearRightTalonSRX.setNeutralMode(K_MODE);

    rearLeftTalonSRX.configOpenloopRamp(kRampUpRate);
    rearLeftTalonSRX.setNeutralMode(K_MODE);


  
  } // *********************** End of roboInit **********************************
  
  /**
   * When in teleop this function is called periodicly
   */
  @Override
  public void teleopPeriodic() {

    // If I did this right, this should allow for direction of travel to be set by using the left joystick
    // while the rotation of the robot is set by the right stick on the controller.

    m_robotDrive.driveCartesian(m_controllerDriver.getRawAxis(1), 
                                m_controllerDriver.getRawAxis(0), 
                                m_controllerDriver.getRawAxis(4));

    //System.out.println(robotGyro.GetHeading());
    SmartDashboard.putNumber("Gyro: ", MyGyro.GetHeading());

    
  } // ************************** End of teleopPeriodic *************************
    
  /**
   * testPeriodic function is called periodicly when the DS is 
   * in test mode.
   */
  @Override
  public void testPeriodic(){
  // Ok, so how do we read that a button has been pressed?  Also can we output it to a dashboard?
  //  public static final String ButtonStatus = "Button Pressed:";

    //This block of code should be moved down to
    if (m_controllerDriver.getRawButtonPressed(1)){
      System.out.println("Button A Pressed");
      SmartDashboard.putString("Button A = ", "I was pushed");
    }
    else if (m_controllerDriver.getRawButtonReleased(1)){
      System.out.println("Button A Released");
      SmartDashboard.putString("Button A = " , "I was released");
    }


    
  } // ************************ End of testPeriodic **************************
}
