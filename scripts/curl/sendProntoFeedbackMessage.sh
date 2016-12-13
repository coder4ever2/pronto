echo $1;
curl -X POST 'https://api.twilio.com/2010-04-01/Accounts/AC02df9ce1610adb63ea980d363d435b16/Messages.json' \
--data-urlencode 'To='"$1"  \
--data-urlencode 'From=+14154231013'  \
--data-urlencode 'Body=Thank you for using Pronto Alexa skill. Please provide your feedback so we can improve. It will take less than a minute and we really appreciate your feedback and time - http://i.pronto.live/25BOA6w -- thank you, Pronto team! ' \
-u AC02df9ce1610adb63ea980d363d435b16:0a14bd65dfe9978f8a0547905503c67d
