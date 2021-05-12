Feature: public test at 
    wss://api.staging.neontrading.com

Scenario Outline: Subscribe instrument successfully
		Given def handler = function(msg){return msg.contains('con')}
    And def socket = karate.webSocket('wss://api.staging.neontrading.com',handler)
    When socket.send('connect 21 {     "device": "<deviceId>",     "clientId": "cta",     "clientVersion": "1.0.1",     "platformId": "ios",     "platformVersion": "10.2",     "locale": "de" }')
    And def result = socket.listen(5000)
    Then match result == 'connected'

    
    Given socket.send('sub <id> { "type": "instrument", "id": "DE000BASF111" }')
    When def result = socket.listen(5000)
    Then match result contains <responseStartWith>
    Then match result contains <activation>
    
    Examples:
         |id                      |responseStartWith            | activation
         |1                       |'1 A'                        | '"active":true'  
         |2                       |'2 A'                        | '"active":true'
    