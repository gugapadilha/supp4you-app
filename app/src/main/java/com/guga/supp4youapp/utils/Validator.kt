object Validator {

    fun validateEmail(email: String): Boolean {
        return !(email.isEmpty() || email.isBlank() || !email.contains("@"))
    }

    fun validatePassword(password: String): Boolean {
        return !(password.isEmpty() || password.isBlank())
    }

    fun validateName(name: String): Boolean {
        return !(name.isEmpty() || name.isBlank())
    }

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