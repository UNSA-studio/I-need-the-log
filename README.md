# I need the log!

## Prerequisites
- This mod is modified based on the template files officially released by NeoForge. Therefore, contributor information defaults to the official one unless you manually check the details to see the correct attribution.
- The mod's code is written and compiled by AI, and packaged using GitHub Actions. Some issues may be unusual, so please bear with us.

## Features
This mod allows you to easily export the full game log (from startup to the current moment) when encountering unknown errors or unexpected disconnections without a full crash. Convenient and fast for analysis!

## Privacy Notice
- This mod does not collect any of your important private information. The exported log only contains some performance information about your device. If you do not wish to share this, you can manually delete the entire section named "Device information" from the exported log.

## A Word of Caution
- It is recommended not to try it lightly!
- For some reason, the AI would always reliably throw errors on the Mixin class—quite hopeless. Eventually, I had to force the AI to change to another approach, something like using a string instead; and as soon as it was replaced, it worked. So frustrating (  
- The development of the features was also bumpy. The AI would go haywire—several classes never worked properly. Whenever it dared to touch Mixin, the whole thing would error out. Absolutely delightful (  
- So I suggest you don't try it lightly.