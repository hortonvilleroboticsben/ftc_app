package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.I2cSensor;
import com.qualcomm.robotcore.util.TypeConversion;

@SuppressWarnings({"WeakerAccess", "unused"})
@I2cSensor(name = "I2C Temperature & Distance Sensor", description = "DF Robotics I2C Temperature & Distance Sensor", xmlTag = "I2CTemp&DistSensor")
public class DFRoboticsGravityI2CTemperatureandDistanceSensor extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    public enum Register
    {
        DEVICE_ADDRESS(0x00),
        PRODUCT_ID(0x01),
        VERSION_NUMBER(0x02),
        DISTANCE_HIGH(0x03),
        DISTANCE_LOW(0x04),
        TEMPERATURE_HIGH(0x05),
        TEMPERATURE_LOW(0x06),
        CONFIGURE_REGISTERS(0x07),
        COMMAND_REGISTERS(0x08),
        LAST(COMMAND_REGISTERS.bVal);

        public int bVal;

        Register(int bVal)
        {
            this.bVal = bVal;
        }
    }

    @Override
    protected synchronized boolean doInitialize()
    {
        

        return true;
    }

    protected void setOptimalReadWindow()
    {
        I2cDeviceSynch.ReadWindow readWindow = new I2cDeviceSynch.ReadWindow(
                Register.DEVICE_ADDRESS.bVal,
                Register.LAST.bVal - Register.DEVICE_ADDRESS.bVal + 1,
                I2cDeviceSynch.ReadMode.REPEAT);
        this.deviceClient.setReadWindow(readWindow);
    }

    public final static I2cAddr ADDRESS_I2C_DEFAULT = I2cAddr.create7bit(0x18);

    public DFRoboticsGravityI2CTemperatureandDistanceSensor(I2cDeviceSynch deviceClient)
    {
        super(deviceClient, true);

        this.setOptimalReadWindow();
        this.deviceClient.setI2cAddress(ADDRESS_I2C_DEFAULT);

        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    @Override
    public Manufacturer getManufacturer()
    {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName()
    {
        return "DF Robotics Gravity I2C Temperature and Distance Sensor";
    }

}
