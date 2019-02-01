package frc.robot;
/**
 * A class to try to tone down the dead pan in the joysticks to make
 * the robot less jumpy.
 * Takes in the axis from a joystick, checks if it's under the deadBandrate
 * set the axis to 0.0 and return it.
 */
public class DeadBand {

      public static final double deadBandrate = 0.15;

      /**
       * Method SmoothAxis
       * @param joyStickAxis, the raw Axis from the controller.
       * @return The smoothed joyStickAxis
       * @author Dr-WaSabi (a.k.a. CDR. Riker)
       */
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
            }// ******** end if > *******
      }// ****** end if <

      return joyStickAxis;

      } // *********************** end of SmoothAxis **********************

}// ************************ end of Class DeadBand *********************
