package com.example.arpaul.rideit.GPSUtilities;

public enum GPSErrorCode 
{
    EC_NO_ERROR(0),
    
    EC_DEVICE_NOT_CONFIGURED_PROPERLY(1),
    EC_DEVICE_CONFIGURED_PROPERLY(2),
	
    EC_GOOGLEPLAY_SERVICES_UPDATE_REQUIRED(3),
	
    EC_GPS_HARDWARE_SETUP_NOTAVAILABLE_ONDEVICE(4),
	EC_GPS_HARDWARE_SETUP_AVAILABLE_ONDEVICE(5),
	
	EC_GPS_PROVIDER_NOT_ENABLED(6),
	EC_GPS_PROVIDER_ENABLED(7),
	
	EC_NETWORK_PROVIDER_NOT_ENABLED(8),
	EC_NETWORK_PROVIDER_ENABLED(9),
	
	EC_CUSTOMER_lOCATION_IS_INVAILD(10),
	EC_CUSTOMER_LOCATION_IS_VALID(11),
	
	EC_INTERNETCONNECTION_NOT_AVAILABLE(12),
	EC_INTERNETCONNECTION_AVAILABLE(13),
	
	EC_NO_ADDRESS_FOUND(14),
	EC_ADDRESS_FOUND(15),
	
	EC_UNABLE_TO_FIND_LOCATION(16),
	EC_LOCATION_FOUND(17),
    
	EC_NO_LATLONG_FOUND(18),
	EC_LATLONG_FOUND(19),
	
	EC_TIME_EXCEEDED(18),
    EC_SUCCESS(19),
    EC_GENERAL_FAILURE(20);
    
	private final int errCode;

	private GPSErrorCode(int inCode) {
		errCode = inCode;
	}

	/**
	 * @brief Method is used to get integer value of respective ErrorCode
	 *        enumeration.
	 * @return @c integer value respective ErrorCode enumeration
	 */
	public int getErrorCodeValue() {
		return errCode;
	}

	public static GPSErrorCode fromInt(int val) {
		for (GPSErrorCode ec : GPSErrorCode.values())
			if (ec.getErrorCodeValue() == val)
				return ec;

		return EC_GENERAL_FAILURE;
	}
}
