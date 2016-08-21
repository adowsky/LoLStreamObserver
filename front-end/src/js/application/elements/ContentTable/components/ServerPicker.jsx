import React from 'react';

export default class ServerPicker extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        let disabled = this.props.disabled;
        let value = this.props.value;
        return <span className="select-wrapper">
            <select value={value} onChange={this.props.handler} disabled={disabled}>
                {ServerPicker.availableServers.map(
                    (server) => <option key={server} value={server}>{server}</option>
                )}
            </select>
        </span>
    }
}

ServerPicker.defaultProps = {
    value: 'EUNE'
};

ServerPicker.propTypes = {
    value: React.PropTypes.string.isRequired,
    handler: React.PropTypes.func.isRequired
};

ServerPicker.availableServers = [
    'EUNE', 'EUW', 'NA', 'TR', 'RU', 'OCE', 'BR', 'JP'
];