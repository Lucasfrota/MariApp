# MariApp

O projeto esta dividido em duas partes

- Servidor (que foi construido em python utilizando o framework Web2py)
- Aplicativo (que foi construido no Android studio utilizando a linguagem Kotlin)

Para utilizar o app é necessario primeiro iniciar o servidor, para isso é precisso ter a pasta "web2py",
que pode ser baixada atraves do link:

http://www.web2py.com/init/default/download 

Uma vez baixado o web2py é necessario que o arquivo MariAppPy (que esta disponivel neste repositorio)
seja copiado para a pasta "applications" que se enconra dentro da pasta web2py, baixada anteriormente.
Uma vez que estes passos foram seguidos é necessario iniciarr o web2py, para isso basta clicar duas 
vezes no arquivo "web2py.py", tambem presente na pasta web2py.

Ao clicar no arquivo "web2py.py" um menu sera aberto, neste menu são exibidas varias opçoes, a opção
"Public(0.0.0.0)" deve ser escolhida (esta é a ultima opção do menu quando lido de cima para baixo).
em seguida o campo "Server Port" deve ser preenchido com o valor "8000", o preenchimento do campo
"Choose Password" serve apenas para definir uma senha para que as pessoas que estao no mesmo wi-fi
nao alterem o codigo, seu preenchimento é opcional.

Agora temos o servidor ligado, o proximo passo é colocar o endereço do servidor no aplicativo,
tendo o app instalado em um celular é necessario selecionar a opção "Configurações" no menu inferior,
preencher o campo com o endereço do servidor, e apertar o botão "Alterar", o endereço colocado sera
salvo e nao mudara ate que este processo seja repetido mudando o valor anteriormente colocado
