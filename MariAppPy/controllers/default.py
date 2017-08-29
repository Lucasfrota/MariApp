# -*- coding: utf-8 -*-
# this file is released under public domain and you can use without limitations

# -------------------------------------------------------------------------
# This is a sample controller
# - index is the default action of any application
# - user is required for authentication and authorization
# - download is for downloading files uploaded in the db (does streaming)
# -------------------------------------------------------------------------

import json

def index():
    """
    example action using the internationalization operator T and flash
    rendered by views/default/index.html or views/generic.html

    if you need a simple wiki simply replace the two lines below with:
    return auth.wiki()
    """
    response.flash = T("Hello World")
    return dict(message=T('Welcome to web2py!'))


def user():
    """
    exposes:
    http://..../[app]/default/user/login
    http://..../[app]/default/user/logout
    http://..../[app]/default/user/register
    http://..../[app]/default/user/profile
    http://..../[app]/default/user/retrieve_password
    http://..../[app]/default/user/change_password
    http://..../[app]/default/user/bulk_register
    use @auth.requires_login()
        @auth.requires_membership('group name')
        @auth.requires_permission('read','table name',record_id)
    to decorate functions that need access control
    also notice there is http://..../[app]/appadmin/manage/auth to allow administrator to manage users
    """
    return dict(form=auth())

def adicionar_parente():
    nome = request.post_vars['nome_parente']
    dataNascimento = request.post_vars['data_nascimento']
    geracao = request.post_vars['geracao']
    parente = Parentes.insert(
        nome_parente = nome,
        data_nascimento = dataNascimento,
        geracao = geracao
    )
    return response.json(parente)

def get_parente():
    id_parente = request.post_vars['id']
    parente = db(Parentes.id == id_parente).select()
    return response.json(parente)

def delete_parente():
    id_parente = request.post_vars['id']
    db(Parentes.id == id_parente).delete()
    return response.json(True)

def get_parente_geracao():
    geracao = request.post_vars['geracao']
    db(Parentes.geracao == geracao).select()
    return response.json(True)

def update_parente():
    id_parente = request.post_vars['id']
    nome = request.post_vars['nome_parente']
    dataNascimento = request.post_vars['data_nascimento']
    geracao = request.post_vars['geracao']
    db(Parentes.id == id_parente).update(
        nome_parente = nome,
        data_nascimento = dataNascimento,
        geracao = geracao
    )
    return response.json(True)

def lista_parentes():
    parentes = db(Parentes).select()
    return response.json(parentes)

@cache.action()
def download():
    """
    allows downloading of uploaded files
    http://..../[app]/default/download/[filename]
    """
    return response.download(request, db)


def call():
    """
    exposes services. for example:
    http://..../[app]/default/call/jsonrpc
    decorate with @services.jsonrpc the functions to expose
    supports xml, json, xmlrpc, jsonrpc, amfrpc, rss, csv
    """
    return service()
