/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;// <-- Needed for xbox style controllers
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;// <-- New for 2019, takes over for the depricated Iteritive robot
import edu.wpi.first.wpilibj.XboxController;// <-- For using a gamepad controller
import edu.wpi.first.wpilibj.drive.MecanumDrive;// <-- Needed for the drive base.

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.*;// <-- gets us access to WPI_TalonSRX which works with wpilibj.drive.Mecanum

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;// <-- For writing data back to the drivers station.
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.  It's been modifed to call the WPI_TalonSRX controllers, which use the can bus, 
 * instead of PWM.  
 */
public class Robot extends TimedRobot {



  private static final int kElev1Motor = 1;
  private static final int kGripperAngle = 7;
  private static final int kElev2Motor = 4;

  // Pneumatic Ports
  private static final int kPCMPort = 0; // <-- Can id # for the PCM
  private static final int kGrabberOpen = 2;
  private static final int kGrabberClose = 3;
  
  private static final int kSlideOpen = 0;
  private static final int kSlideClose = 1;

  // What ever USB port we have the controller plugged into.
  private static final int kGamePadChannel = 0;
  private static final int kXboxChannel = 1;

  //Lets map out the buttons
  private static final int kXboxButtonA = 1;
  private static final int kXboxButtonB = 2;
  private static final int kXboxButtonX = 3;
  private static final int kXboxButtonY = 4;

  private static final int kXboxButtonLB = 5; // <-- Left Button
  private static final int kXboxButtonRB = 6; // <-- Right Button
  private static final int kXboxButtonLT = 2; // <-- Left Trigger
  private static final int kXboxButtonRT = 3; // <-- Right Trigger

  private static final double kRampUpRate = 0.5; // The rate that the motor controller will speed up to full;
  private static final NeutralMode K_MODE = NeutralMode.Brake; // Setting the talons neutralmode to brake

  private MecanumDrive m_robotDrive;

  private GenericHID m_Driver;
  private GenericHID m_Operator;

  private DeadBand m_stick;

  private ADXRS450_Gyro  MyGyro;
  private boolean HasBeenRun = false;
  private boolean debug = true; //Debug flag to print stuff out to the rio logger if set to true

  Compressor myCompressor = new Compressor(kPCMPort);
  
  //DoubleSolenoid Grabber = new DoubleSolenoid(kGrabberOpen, kGrabberClose);
  DoubleSolenoid myGrabber = new DoubleSolenoid(kPCMPort, 2, 3);
  DoubleSolenoid myTable = new DoubleSolenoid(kPCMPort, kSlideOpen, kSlideClose);
  
  WPI_VictorSPX elev1Motor = new WPI_VictorSPX(kElev1Motor);
  WPI_VictorSPX elev2Motor = new WPI_VictorSPX(kElev2Motor);
  WPI_VictorSPX gripAngleMotor = new WPI_VictorSPX(7);

  DigitalInput elev1Top = new DigitalInput(3);
  DigitalInput elev1Bottom = new DigitalInput(2);
  DigitalInput elev2Top = new DigitalInput(1);
  DigitalInput elev2Bottom = new DigitalInput(0);

  boolean canMoveElev1, canMoveElev2;
  /**
   * This function if called when the robot boots up.
   * It creates the objects that are called by the other robot functions.
   */
  @Override
  public void robotInit() {

    Spark frontleft = new Spark(8);
    Spark frontright = new Spark(7);
    Spark rearleft = new Spark(6);
    Spark rearright = new Spark(9);


    
    m_robotDrive = new MecanumDrive(frontleft, rearleft, frontright, rearright);
    
    
    m_Driver = new XboxController(kXboxChannel); // <-- Driver controller
    m_Operator = new XboxController(kGamePadChannel); //<-- Operator controller


    MyGyro = new ADXRS450_Gyro();//This should create a new Gyro object called MyGyro

    MyGyro.calibrate(); //Run the init method, to reset and calibrate the gyro.
    MyGyro.reset();

    if (MyGyro.isConnected()){
      SmartDashboard.putNumber("Gryo", MyGyro.getAngle());
      if(debug){System.out.println("Gyro is connected");}
    } else {
      DriverStation.reportError("Error - No Gyro", MyGyro.isConnected());
    }

        
    // Invert the left side motors.
    // You may need to change or remove this to match your robot.
    
    rearright.setInverted(true);
    frontleft.setInverted(true);

    
    gripAngleMotor.setNeutralMode(NeutralMode.Brake);
    gripAngleMotor.stopMotor();
    elev1Motor.setNeutralMode(NeutralMode.Brake);
    elev1Motor.stopMotor();
    elev2Motor.setNeutralMode(NeutralMode.Brake);
    elev2Motor.stopMotor();

    m_stick = new DeadBand();
    
    myCompressor.enabled();

    canMoveElev1 = true;
    canMoveElev2 = true;
    
    //myGrabber.set(DoubleSolenoid.Value.kOff);
    //myTable.set(Value.kOff);



  
  } // *********************** End of roboInit **********************************
  
  /**
   * When in teleop this function is called periodicly
   */
  @Override
  public void teleopPeriodic() {
    // Need to come up with a way to tone down the joysticks

    // If I did this right, this should allow for direction of travel to be set by using the left joystick
    // while the rotation of the robot is set by the right stick on the controller.
    // I've also inverted Axis 0 so when you push left, the robot slides left. Right, robot slides right.
    
    m_robotDrive.driveCartesian(m_stick.SmoothAxis(m_Driver.getRawAxis(1)), 
                                m_stick.SmoothAxis(-m_Driver.getRawAxis(0)), 
                                m_stick.SmoothAxis(m_Driver.getRawAxis(4)));

    SmartDashboard.putNumber("Gyro:", MyGyro.getAngle());    
                                
    // ***************** Open Grabber *****************
    if (m_Operator.getRawButtonPressed(kXboxButtonX)) {
      System.out.println("Oper X button pressed, Grabber OPEN!");
      myGrabber.set(DoubleSolenoid.Value.kForward);
    }

    // *************** Close Grabber ****************
    if (m_Operator.getRawButtonPressed(kXboxButtonB)) {
      System.out.println("Oper B button pressed, Grabber CLOSE!");
      myGrabber.set(DoubleSolenoid.Value.kReverse);
    }   

    // ************* Move Elevator 1 Up/Down *******************
    if ((m_Operator.getRawAxis(1) > 0) && (elev1Top.get() == false)){
      elev1Motor.set(m_Operator.getRawAxis(1));
    }
    else if ((m_Operator.getRawAxis(1) > 0) && (elev1Top.get() == true)){
      elev1Motor.stopMotor();
    }

    if ((m_Operator.getRawAxis(1) < 0) && (elev1Bottom.get() == false)){
      elev1Motor.set(m_Operator.getRawAxis(1));
    }
    else if ((m_Operator.getRawAxis(1) < 0) && (elev1Bottom.get() == true)){
      elev1Motor.stopMotor();
    }

    // first try, didn't work.
    /*if ((m_Operator.getRawAxis(1) != 0)  & (elev1Top.get() == true) | (elev1Bottom.get() == true)) {
        //Going Up
        elev1Motor.set(m_Operator.getRawAxis(1));
    }
    else if ((m_Operator.getRawAxis(1) == 0) | (elev1Top.get() == false) | (elev1Bottom.get() == false)){
      elev1Motor.stopMotor();
    }
*/
    // ************* Limit Switch Elevator 1 ********************
    if (elev1Top.get() == false) { 
        System.out.println("Elev Top Switch Pressed");
    } 


    // *************** Move Elevator 2 UP/DOwn *************
    if (m_Operator.getRawAxis(5) != 0) {
      //Going Up
      elev2Motor.set(m_Operator.getRawAxis(5));  
    }
    else if (m_Operator.getRawAxis(5) == 0) {
      elev2Motor.stopMotor();
    }


    // ********************* Gripper Motor Down ******************
    if (m_Operator.getRawButtonPressed(kXboxButtonRB)){
      System.out.println("Right Trigger Pulled ");
      gripAngleMotor.set(-1.0);
    }
    else if (m_Operator.getRawButtonReleased(kXboxButtonRB)) {
      gripAngleMotor.stopMotor();
    }
    // ********************* Gripper Motor Up *******************
    if (m_Operator.getRawButtonPressed(kXboxButtonLB)){
      System.out.println("Left Trigger Pulled");
      gripAngleMotor.set(1.0);
    }
    else if (m_Operator.getRawButtonReleased(kXboxButtonLB)){
      gripAngleMotor.stopMotor();
    }

    } // ************************** End of teleopPeriodic *************************
    
    /**
     * testPeriodic function is called periodicly when the DS is 
     * in test mode.
     */
    @Override
    public void testPeriodic(){


    SmartDashboard.putNumber("Gyro:", MyGyro.getAngle());

    if (m_Driver.getRawButtonReleased(kXboxButtonA)) {
      SmartDashboard.putNumber("Gyro:", MyGyro.getAngle());
      System.out.println(MyGyro.getAngle());
      HasBeenRun = MyGyro.isConnected();
      if (debug){
        if (HasBeenRun == true){
          System.out.println("Gyro is connected");
        } else {
          System.out.println("Gyro? We got no stinkin GYRO! - Gyro not connected");
        }
      } // *** end if debug ***
      
    }

    // Testing out Pneumatic Grabby thing
  

  } // ************************ End of testPeriodic **************************

} 
