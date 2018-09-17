# Java异常

- finally：只有finally块，执行完之后，才会回来执行try和catch块中的return和throw语句，如果finally中使用了return或者throw等终止方法的语句，则就不会跳回执行，直接停止。