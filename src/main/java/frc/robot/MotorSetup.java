/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;;

/**
 * Will do the inital setup of the talonSRX motor controllers.
 * Setting the talons ramp rate. Also setting the neutal mode 
 * to coast.
 */
public class MotorSetup {
    public void init(TalonSRX motor){
        motor.configOpenLoopRamp(0.5);
        motor.setNeutralMode(coast);
    
    }
}
