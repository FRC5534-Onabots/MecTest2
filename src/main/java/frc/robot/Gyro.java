/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

/**
 * Gyro Obect
 */
public class Gyro {
    public static ADXRS450_Gyro Gyroscope = new ADXRS450_Gyro();

    public static void Init() {
        Gyroscope.calibrate();
        Gyroscope.reset();

    } // ************************ End of Gyroscope.Init **************************

    
	public double GetHeading() {
		return Gyroscope.getAngle();
    } // ********************** end of Gyroscope.GetHeading **********************
    
    public static void ResetGyro(){
        Gyroscope.reset();

    } // ************************** end of ResetGyro ***********************


}// ************************* End of Gyro Class **************************
