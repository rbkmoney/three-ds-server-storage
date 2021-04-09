# Three-ds-server-storage

[3DSS Docker Compose description](https://github.com/rbkmoney/three-ds-server-compose#three-ds-server-compose)

## важно!
значения карточных диапазонов `cardRange.getRangeStart(), cardRange.getRangeEnd()`, а также номер аккаунта `accountNumber` должны быть замаскированы.
для этих целей в проекте есть `CardRangeWrapper.toStringHideCardRange` для `cardRange` и отдельный метод `AccountNumberUtils.hideAccountNumber`
