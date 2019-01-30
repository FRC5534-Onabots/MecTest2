/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;



/**
 * a class to try to tone down the dead pan in the joysticks to make
 * the robot less jumpy
 */


public class DeadBand {
    public static final double deadBandrate = 0.2;

    public double SmoothAxis(double joyStickAxis){
        
        if (Math.abs(joyStickAxis) < deadBandrate) {
            joyStickAxis = 0.0;
         }
         else {
            if (joyStickAxis>0.0) {
               joyStickAxis = (joyStickAxis - deadBandrate) / (1.0 - deadBandrate);
            }
            else {
               joyStickAxis = (joyStickAxis - -deadBandrate) / (1.0 - deadBandrate);
            }
         }

        return joyStickAxis;
    }
}
