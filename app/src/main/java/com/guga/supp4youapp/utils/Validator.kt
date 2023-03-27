object Validator {
    fun validateCode() : String{
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val codeSize = 6
        val random = java.util.Random()
        val code = CharArray(codeSize)

        for (i in 0 until codeSize) {
            code[i] = letters[random.nextInt(letters.length)]
        }
        return String(code)
    }

}