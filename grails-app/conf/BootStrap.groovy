class BootStrap {

    def init = { servletContext ->
        //TimeZone.setDefault(TimeZone.getTimeZone('GMT'))
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
    def destroy = {
    }
}
