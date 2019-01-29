/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

/**
 * Add your docs here.
 */
public class Gyro {
    public static ADXRS450_Gyro Gyroscope = new ADXRS450_Gyro();

    public static void Init() {
        Gyroscope.calibrate();
        Gyroscope.reset();

    } // ************************ End of Gyroscope.Init **************************


}// ************************* End of Gyro Class **************************
