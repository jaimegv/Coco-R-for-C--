rem prueba5.bas (incorrecta)

sub dividir(a, b)
	let resultado = a / b
end sub

print "Ingrese un numero:"
input a

print "Ingrese un numero:"
input b

rem numero de argumentos erroneo
call dividir(a)
print "La division es: " ; resultado

end
