import React from 'react';

export default class HeaderView extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <header className="header">
                <object className="logo" data={this.props.logoLocation} type="image/png">
                    {this.props.logoAltName}
                </object>
                <nav className="navigation">
                    {this.props.menu.map((obj) =>
                        <button key={obj.name} onClick={obj.action}>
                            {obj.name}
                            {(obj.hasOwnProperty("customObject")) ? obj.customObject : null}
                        </button>
                    )}
                </nav>
            </header>);
    }
}

HeaderView.defaultProps = {
    logoLocation: '/images/logo.png',
    logoAltName: 'LoLStreamObserver'
};