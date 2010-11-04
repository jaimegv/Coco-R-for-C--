rem prueba6.bas (incorrecta)

sub elevar(a, b)
	rem error al operar con una cadena
	let resultado = a ^ b$
end sub

print "Ingrese un numero:"
input a

print "Ingrese un numero:"
input b

call elevar(a, b)
print "La elevar es: " ; resultado

end
