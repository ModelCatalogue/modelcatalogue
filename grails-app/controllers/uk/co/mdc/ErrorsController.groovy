package uk.co.mdc

class ErrorsController {

def error403 = {}

def error404 = {}

def error500 = { render view: '/error' } 

}


