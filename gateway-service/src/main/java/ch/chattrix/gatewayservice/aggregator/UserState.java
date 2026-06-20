package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.types.UserData;

public class UserState {
        UserData user = new UserData();
        boolean authReceived;
        boolean userReceived;
}
