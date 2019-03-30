/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;// <-- Needed for xbox style controllers
import edu.wpi.first.wpilibj.TimedRobot;// <-- New for 2019, takes over for the depricated Iteritive robot
import edu.wpi.first.wpilibj.XboxController;// <-- For using a gamepad controller
import edu.wpi.first.wpilibj.drive.MecanumDrive;// <-- Needed for the drive base.

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.*;// <-- gets us access to WPI_TalonSRX which works with wpilibj.drive.Mecanum
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;// <-- For writing data back to the drivers station.
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.  It's been modifed to call the WPI_TalonSRX controllers, which use the can bus, 
 * instead of PWM.  
 */
public class Robot extends TimedRobot {

  // These will need to be updated to the CAN Ids of the WPI_TalonSRX's
  private static final int kFrontLeftChannel = 2;
  private static final int kRearLeftChannel = 3;
  private static final int kFrontRightChannel = 5;
  private static final int kRearRightChannel = 6;

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
  private static final int kOperatorController = 1;
  private static final int kDriverController = 0;

  //Lets map out the buttons
  // private static final int kXboxButtonA  = 1;
  // private static final int kXboxButtonB = 2;
  // private static final int kXboxButtonX = 3;
  // private static final int kXboxButtonY = 4;

  // private static final int kXboxButtonLB = 5; // <-- Left Button
  // private static final int kXboxButtonRB = 6; // <-- Right Button
  // private static final int kXboxButtonLT = 2; // <-- Left Trigger
  // private static final int kXboxButtonRT = 3; // <-- Right Trigger

  private static final double kRampUpRate = 0.5; // The rate that the motor controller will speed up to full;
  private static final NeutralMode K_MODE = NeutralMode.Brake; // Setting the talons neutralmode to brake

  private MecanumDrive m_robotDrive;

  // private GenericHID m_Driver;
  // private GenericHID m_Operator;
  private XboxController m_Driver;
  private XboxController m_Operator;

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



    //m_robotDrive = new MecanumDrive(frontLeftTalonSRX, rearLeftTalonSRX, frontRightTalonSRX, rearRightTalonSRX);
    m_robotDrive = new MecanumDrive(frontLeftTalonSRX, rearLeftTalonSRX, frontRightTalonSRX, rearRightTalonSRX);

    // m_controllerDriver = new Joystick(kJoystickChannel);
    
    m_Driver = new XboxController(kDriverController); // <-- Driver controller
    m_Operator = new XboxController(kOperatorController); //<-- Operator controller


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
    //frontLeftTalonSRX.setInverted(true);
    //rearRightTalonSRX.setInverted(true);

    /**
     * Added to test out setting talon config some settings internal
     * to the TalonSRXs
     */
    m_robotDrive.setSafetyEnabled(true);
    m_robotDrive.setExpiration(0.1);
    m_robotDrive.setMaxOutput(1.0);
    
    gripAngleMotor.setNeutralMode(NeutralMode.Brake);
    gripAngleMotor.stopMotor();
    elev1Motor.setNeutralMode(NeutralMode.Brake);
    elev1Motor.stopMotor();
    elev2Motor.setNeutralMode(NeutralMode.Brake);
    elev2Motor.stopMotor();

    m_stick = new DeadBand();
    
    myCompressor.enabled();
    
    //myGrabber.set(DoubleSolenoid.Value.kOff);
    //myTable.set(Value.kOff);



  
  } // *********************** End of roboInit **********************************
  

  @Override
  public void autonomousPeriodic() {
    
    m_robotDrive.driveCartesian(m_Driver.getX(Hand.kLeft), 
                                -m_Driver.getY(Hand.kLeft), 
                                m_Driver.getX(Hand.kRight));
    
                                
    // ***************** Open Grabber *****************
    if (m_Operator.getXButtonPressed()) {
      // System.out.println("Oper X button pressed, Grabber OPEN!");
      myGrabber.set(DoubleSolenoid.Value.kForward);
    }

    // *************** Close Grabber ****************
    if (m_Operator.getBButtonPressed()) {
      // System.out.println("Oper B button pressed, Grabber CLOSE!");
      myGrabber.set(DoubleSolenoid.Value.kReverse);
    }   

    // ************* Move TABLE forward *******************
    if (m_Driver.getYButtonPressed()) {
      myTable.set(Value.kForward);
    }
    // ************* Move TABLE backward ******************
    if (m_Driver.getAButtonPressed()) {
      myTable.set(Value.kReverse);
    }

    // ************* Move Elevator 1 Up/Down *******************
    if (m_Operator.getY(Hand.kLeft) != 0){
        //Going Up
        elev1Motor.set(m_Operator.getY(Hand.kLeft));
    }
    else if (m_Operator.getY(Hand.kLeft) == 0) {
      elev1Motor.stopMotor();
    }


    // *************** Move Elevator 2 UP/DOwn *************
    if (m_Operator.getY(Hand.kRight) != 0) {
      //Going Up
      elev2Motor.set(m_Operator.getY(Hand.kRight));  
    }
    else if (m_Operator.getY(Hand.kRight) == 0) {
      elev2Motor.stopMotor();
    }


    // ********************* Gripper Motor Down ******************
    if (m_Operator.getBumperPressed(Hand.kRight)){
      // System.out.println("Right Trigger Pulled ");
      gripAngleMotor.set(-1.0);
    }
    else if (m_Operator.getBumperReleased(Hand.kRight)) {
      gripAngleMotor.stopMotor();
    }
    // ********************* Gripper Motor Up *******************
    if (m_Operator.getBumperPressed(Hand.kLeft)){
      // System.out.println("Left Trigger Pulled");
      gripAngleMotor.set(1.0);
    }
    else if (m_Operator.getBumperReleased(Hand.kLeft)){
      gripAngleMotor.stopMotor();
    }

  }
  /**
   * When in teleop this function is called periodicly
   */
  @Override
  public void teleopPeriodic() {
    
    m_robotDrive.driveCartesian(m_Driver.getX(Hand.kLeft), 
                                -m_Driver.getY(Hand.kLeft), 
                                m_Driver.getX(Hand.kRight));
    
                                
    // ***************** Open Grabber *****************
    if (m_Operator.getXButtonPressed()) {
      // System.out.println("Oper X button pressed, Grabber OPEN!");
      myGrabber.set(DoubleSolenoid.Value.kForward);
    }

    // *************** Close Grabber ****************
    if (m_Operator.getBButtonPressed()) {
      // System.out.println("Oper B button pressed, Grabber CLOSE!");
      myGrabber.set(DoubleSolenoid.Value.kReverse);
    }   

    // ************* Move TABLE forward *******************
    if (m_Driver.getYButtonPressed()) {
      myTable.set(Value.kForward);
    }
    // ************* Move TABLE backward ******************
    if (m_Driver.getAButtonPressed()) {
      myTable.set(Value.kReverse);
    }

    // ************* Move Elevator 1 Up/Down *******************
    if (m_Operator.getY(Hand.kLeft) != 0){
        //Going Up
        elev1Motor.set(m_Operator.getY(Hand.kLeft));
    }
    else if (m_Operator.getY(Hand.kLeft) == 0) {
      elev1Motor.stopMotor();
    }


    // *************** Move Elevator 2 UP/DOwn *************
    if (m_Operator.getY(Hand.kRight) != 0) {
      //Going Up
      elev2Motor.set(m_Operator.getY(Hand.kRight));  
    }
    else if (m_Operator.getY(Hand.kRight) == 0) {
      elev2Motor.stopMotor();
    }


    // ********************* Gripper Motor Down ******************
    if (m_Operator.getBumperPressed(Hand.kRight)){
      // System.out.println("Right Trigger Pulled ");
      gripAngleMotor.set(-1.0);
    }
    else if (m_Operator.getBumperReleased(Hand.kRight)) {
      gripAngleMotor.stopMotor();
    }
    // ********************* Gripper Motor Up *******************
    if (m_Operator.getBumperPressed(Hand.kLeft)){
      // System.out.println("Left Trigger Pulled");
      gripAngleMotor.set(1.0);
    }
    else if (m_Operator.getBumperReleased(Hand.kLeft)){
      gripAngleMotor.stopMotor();
    }

    } // ************************** End of teleopPeriodic *************************
    
    /**
     * testPeriodic function is called periodicly when the DS is 
     * in test mode.
     */
    @Override
    public void testPeriodic(){

    // Testing out Pneumatic Grabby thing
  

  } // ************************ End of testPeriodic **************************

} 
