{{/*
Expand the name of the chart.
*/}}
{{- define "inventorymanagement.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "inventorymanagement.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "inventorymanagement.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "inventorymanagement.labels" -}}
helm.sh/chart: {{ include "inventorymanagement.chart" . }}
{{ include "inventorymanagement.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "inventorymanagement.selectorLabels" -}}
app.kubernetes.io/name: {{ include "inventorymanagement.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "inventorymanagement.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "inventorymanagement.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Templates the postgres db password for the inventorymanagement user
*/}}
{{- define "inventorymanagement.dbPassword" -}}
{{- $msg := "inventorymanagement db user password must be defined" -}}
{{- required $msg .Values.postgres.dbPassword -}}
{{- end -}}

{{/*
Templates the sftp server username for inventorymanagement
*/}}
{{- define "inventorymanagement.sftpUsername" -}}
{{- $msg := "inventorymanagement sftpUsername must be defined" -}}
{{- required $msg .Values.sftp.username -}}
{{- end -}}

{{/*
Templates the sftp server password for inventorymanagement
*/}}
{{- define "inventorymanagement.sftpPassword" -}}
{{- $msg := "inventorymanagement sftp.password must be defined" -}}
{{- required $msg .Values.sftp.password -}}
{{- end -}}
