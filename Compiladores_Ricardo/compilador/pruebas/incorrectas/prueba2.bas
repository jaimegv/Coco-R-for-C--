rem prueba2.bas (incorrecta)

sub sumar(a, b)
	rem caracter ilegal ?
	let resultado = a ? b
end sub

print "Ingrese un numero:"
input a

print "Ingrese un numero:"
input b

call sumar(a, b)
print "La suma es: " ; resultado

end
