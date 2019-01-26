/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID;// <-- Needed for xbox style controllers
//import edu.wpi.first.wpilibj.Talon;// <-- Removed becuase this version of Talon uses PWM, instead of CAN.

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import com.ctre.phoenix.motorcontrol.can.*;// <-- gets us access to WPI_TalonSRX which works with wpilibj.drive.Mecanum

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.  It's been modifed to call the WPI_TalonSRX controllers, which use the can bus, 
 * instead of PWM.  
 */
public class Robot extends TimedRobot {
  // These will need to be updated to the CAN Ids of the TalonSRX's
  private static final int kFrontLeftChannel = 2;
  private static final int kRearLeftChannel = 3;
  private static final int kFrontRightChannel = 1;
  private static final int kRearRightChannel = 0;

  // What ever USB port we have the controller plugged into.
  private static final int kGamePadChannel = 0;

  private MecanumDrive m_robotDrive;

  private GenericHID m_controllerDriver;
  private GenericHID m_controllerOperator;// <-- We might have so many controles that we need an operator
  //private Joystick m_controllerDriver;

  @Override
  public void robotInit() {
    WPI_TalonSRX frontLeftTalonSRX = new WPI_TalonSRX(kFrontLeftChannel);
    WPI_TalonSRX frontRightTalonSRX = new WPI_TalonSRX(kFrontRightChannel);
    WPI_TalonSRX rearLeftTalonSRX = new WPI_TalonSRX(kRearLeftChannel);
    WPI_TalonSRX rearRightTalonSRX = new WPI_TalonSRX(kRearRightChannel);



    // Invert the left side motors.
    // You may need to change or remove this to match your robot.
    frontLeftTalonSRX.setInverted(true);
    rearLeftTalonSRX.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeftTalonSRX, rearLeftTalonSRX, frontRightTalonSRX, rearRightTalonSRX);

    // m_controllerDriver = new Joystick(kJoystickChannel);
    
    m_controllerDriver = new XboxController(kGamePadChannel);
  
  }

  @Override
  public void teleopPeriodic() {
    // Use the joystick X axis for lateral movement, Y axis for forward
    // movement, and Z axis for rotation.
    
    // This line needs to be tweaked to work with xbox controller thumb sticks.
    //m_robotDrive.driveCartesian(m_controllerDriver.getX(), m_controllerDriver.getY(),m_controllerDriver.getZ(), 0.0);

    // If I did this right, this should allow for direction of travel to be set by using the left joystick
    // while the rotation of the robot is set by the right stick on the controller.
    m_robotDrive.driveCartesian(m_controllerDriver.getRawAxis(1), 
                                m_controllerDriver.getRawAxis(0), 
                                m_controllerDriver.getRawAxis(4));
  }
}
