?- inputPin(P, PIN),
|    setof(R, nodeCharacteristic(P, 'Roles (_JvuuQ9vqEeqNdo_V4bA-xw)', R), ROLES),
|    setof_characteristics(P, PIN, 'AccessPermissions (_k9jB49vTEeqNdo_V4bA-xw)', REQ, S),
|    intersection(REQ, ROLES, []).
P = 'Airline.BookingStorage (_2nivuNvZEeqNdo_V4bA-xw)',
PIN = 'input (_jjMuUNvUEeqNdo_V4bA-xw)',
ROLES = ['Airline (_sJIe0NvTEeqNdo_V4bA-xw)'],
REQ = ['User (_tkFZ4NvTEeqNdo_V4bA-xw)'],
S = ['booking (_nr6Vi9vnEeqNdo_V4bA-xw)', ['selectedFlight (_gPu329vnEeqNdo_V4bA-xw)', ['selectedFlight (_cyM2e9vmEeqNdo_V4bA-xw)', ['filteredFlights (_FDm7OdvbEeqNdo_V4bA-xw)', ['filteredFlights (_yFbZ-dvaEeqNdo_V4bA-xw)'|...]]], ['declassifiedCCD (_3JViS9vmEeqNdo_V4bA-xw)'|_22282]], ['declassifiedCCD (_GEjka9vnEeqNdo_V4bA-xw)', ['selectedFlight (_cyM2e9vmEeqNdo_V4bA-xw)'|_22306], ['ccd direct (_s1BNgOeREeqgzP-TZKi5QA)', ['ccd (_guRqe9vmEeqNdo_V4bA-xw)']]]] ;
P = 'Airline.BookingStorage (_2nivuNvZEeqNdo_V4bA-xw)',
PIN = 'input (_jjMuUNvUEeqNdo_V4bA-xw)',
ROLES = ['Airline (_sJIe0NvTEeqNdo_V4bA-xw)'],
REQ = ['User (_tkFZ4NvTEeqNdo_V4bA-xw)'],
S = ['booking (_nr6Vi9vnEeqNdo_V4bA-xw)', ['selectedFlight (_gPu329vnEeqNdo_V4bA-xw)', ['selectedFlight (_cyM2e9vmEeqNdo_V4bA-xw)', ['filteredFlights (_FDm7OdvbEeqNdo_V4bA-xw)', ['filteredFlights (_yFbZ-dvaEeqNdo_V4bA-xw)'|...]]], ['ccd direct (_s1BNgOeREeqgzP-TZKi5QA)'|_21886]], ['declassifiedCCD (_GEjka9vnEeqNdo_V4bA-xw)', ['selectedFlight (_cyM2e9vmEeqNdo_V4bA-xw)'|_21910], ['ccd direct (_s1BNgOeREeqgzP-TZKi5QA)', ['ccd (_guRqe9vmEeqNdo_V4bA-xw)']]]] ;
P = 'Airline.processBooking (_AYs019vnEeqNdo_V4bA-xw)',
PIN = 'input2 (_-A2uUNvTEeqNdo_V4bA-xw)',
ROLES = ['Airline (_sJIe0NvTEeqNdo_V4bA-xw)'],
REQ = ['User (_tkFZ4NvTEeqNdo_V4bA-xw)'],
S = ['declassifiedCCD (_GEjka9vnEeqNdo_V4bA-xw)', ['selectedFlight (_cyM2e9vmEeqNdo_V4bA-xw)'|_27716], ['ccd direct (_s1BNgOeREeqgzP-TZKi5QA)', ['ccd (_guRqe9vmEeqNdo_V4bA-xw)']]] ;
false.